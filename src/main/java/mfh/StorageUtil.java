package mfh;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
/**
 * 待完成：
 * 1、对于空的检查没有写
 * 2、考虑是否将上传或下载功能返回一个自定义的model,里面可以有相关消息，状态码，versionID，url等必要的信息
 * 有时上传一个文件后，用户需要得到versionID，还需要得到url，这时就要考虑返回一个对象了
 * 3、要加入日志，比如上传、下载成功或者失败后，都要有日志提示
 * 4、异常还需要封装吗？
 * 5、对文件路径没有做校验，如果发生异常，使其直接抛出吗？还是程序处理
 * 6、缺少上传文件夹功能
 * 7、缺少下载文件夹功能
 * 8、缺少显示文件列表功能
 * 9、s3 允许最大连接数50， 需要使用连接池？如果有大文件，可以使用分片下载，但是分片下载会占用多个连接（connection）,
 * com.amazonaws.services.s3.transfer.TransferManagerBuilder#setMultipartCopyThreshold(java.lang.Long) 中提到了分片多占用连接问题。
 * 10、TransferManager 如果关闭，客户端也会关闭；如果不关闭 TransferManger，应该如何管理？
 * 11、TransferManager 默认初始，线程池中只有10个线程（com.amazonaws.services.s3.transfer.internal.TransferManagerUtils#createDefaultExecutorService()）
 */

/**
 * @Author: mfh
 * @Date: 2019-04-17 14:21
 **/
public class StorageUtil {
    private static AmazonS3 s3;

    private StorageUtil() {}

    static {
        s3 = getClient();
    }

    /**
     * 注意：从服务器上取的文件要与保存到磁盘上的文件的文件类型保持一致
     * @param sourceKey         已存在于服务器上的文件（文件名+后缀）
     * @param targetFilePath    文件下载路径
     * @return                  下载结果
     */
    public static Result<String> downloadFile(String sourceKey, String targetFilePath) {
        Result<String> result = buildSuccessResult();
        try {
            downloadFile(sourceKey, new File(targetFilePath));
        } catch (Exception e) {
            result.setCode(ResultStatusEnum.FAILURE);
            result.setEx(e);
        }
        return result;
    }

    /**
     * 注意：从服务器上取的文件要与保存到磁盘上的文件的文件类型保持一致
     * @param sourceKey 已存在于服务器上的文件（文件名+后缀）
     * @param file      保存到磁盘的文件
     * @return          下载结果
     */
    public static Result<String> downloadFile(String sourceKey, File file) {
        Result<String> result = buildSuccessResult();
        GetObjectRequest objectRequest = getObjectRequest(sourceKey);
        try {
            getClient().getObject(objectRequest, file);
        } catch (Exception e) {
            result.setCode(ResultStatusEnum.FAILURE);
            result.setEx(e);
        }
        return result;
    }

    /**
     * 服务器上可以存在相同（名称+类型）的文件，在下载文件时，使用 versionId，可以更精确的定位文件
     * 注意：从服务器上取的文件要与保存到磁盘上的文件的文件类型保持一致
     * @param sourceKey         已存在于服务器上的文件（文件名+后缀）
     * @param versionId         文件版本id
     * @param targetFilePath    文件下载路径
     * @return                  下载结果
     */
    public static Result<String> downloadFile(String sourceKey, String versionId, String targetFilePath) {
        Result<String> result = buildSuccessResult();
        try {
            downloadFile(sourceKey, versionId, new File(targetFilePath));
        } catch (Exception e) {
            result.setCode(ResultStatusEnum.FAILURE);
            result.setEx(e);
        }
        return result;
    }

    /**
     * 服务器上可以存在相同（名称+类型）的文件，在下载文件时，使用 versionId，可以更精确的定位文件
     * 注意：从服务器上取的文件要与保存到磁盘上的文件的文件类型保持一致
     * @param sourceKey     已存在于服务器上的文件（文件名+后缀）
     * @param versionId     文件版本id
     * @param file          保存到磁盘的文件
     * @return              下载结果
     */
    public static Result<String> downloadFile(String sourceKey, String versionId, File file) {
        Result<String> result = buildSuccessResult();
        GetObjectRequest objectRequest = getObjectRequest(sourceKey, versionId);
        try {
            getClient().getObject(objectRequest, file);
        } catch (Exception e) {
            result.setCode(ResultStatusEnum.FAILURE);
            result.setEx(e);
        }
        return result;
    }

    /**
     * 服务器上可以存在相同（名称+类型）的文件，在下载文件时，使用 versionId，可以更精确的定位文件
     * @param bucketName        桶名称
     * @param sourceKey         已存在于服务器上的文件（文件名+后缀）
     * @param versionId         文件版本id
     * @param targetFilePath    文件下载路径
     * @return                  下载结果
     */
    public static Result<String> downloadFile(String bucketName, String sourceKey, String versionId, String targetFilePath) {
        Result<String> result = buildSuccessResult();
        GetObjectRequest objectRequest = new GetObjectRequest(bucketName, sourceKey, versionId);
        try {
            getObject(objectRequest, targetFilePath);
        } catch (Exception e) {
            result.setCode(ResultStatusEnum.FAILURE);
            result.setEx(e);
        }
        return result;
    }

    public static void downloadFile(String key, String fileName, HttpServletResponse response) {
        S3Object object = getClient().getObject(getObjectRequest(key));
        InputStream input = object.getObjectContent();
        OutputStream out = null;
        byte[] data;
        int len;
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            data = new byte[input.available()];
//            out = response.getOutputStream();
            out = new FileOutputStream(new File(fileName));
            while ((len = input.read(data)) != -1) {
                out.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(out);
            closeStream(input);
        }
    }

    /**
     * @param sourceKey         已存在于服务器上的文件（文件名+后缀）
     * @param targetFilePath    文件下载路径
     * @return                  下载结果
     */
    public static Result<String> downloadFileBuffer(String sourceKey, String targetFilePath) {
        Result<String> result = buildSuccessResult();
        S3Object object = getClient().getObject(getObjectRequest(sourceKey));
        BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
        OutputStreamWriter out = null;
        String line;
        try {
            out = new OutputStreamWriter(new FileOutputStream(new File(targetFilePath)), StandardCharsets.UTF_8);
            while ((line = reader.readLine()) != null) {
                out.write(line + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            result.setCode(ResultStatusEnum.FAILURE);
            result.setEx(e);
        } finally {
            closeStream(out);
            closeStream(reader);
        }
        return result;
    }

    /**
     *
     * @param keyPrefix s3 上的文件夹名称，不需要加/
     * @param destinationDirectory 目标文件夹，如果不存在会自动创建
     */
    public static void downloadDirectory(String keyPrefix, String destinationDirectory) {
        //TODO keyPrefix 如果不存在 s3 上，也不会报错，是否需要先做验证？
        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(getClient())
                .build();
        // TransferManager 采用异步方式进行处理，因此该调用会立即返回。
        MultipleFileDownload download = tm.downloadDirectory(Key.BUCKETNAME.val, keyPrefix, new File(destinationDirectory));
        try {
            // 等待下载全部完成。
            download.waitForCompletion();
        } catch (AmazonClientException | InterruptedException amazonClientException) {
            amazonClientException.printStackTrace();
        }
        //在完成操作后，您必须显示关闭TransferManager。
        tm.shutdownNow();
    }

    public static String uploadFile(String absoluteFilePath) {
        return uploadFile(new File(absoluteFilePath));
    }

    public static String uploadFile(File file) {
        return uploadFile(Key.BUCKETNAME.val, file.getName(), file);
    }

    public static String uploadFile(String key, String absoluteFilePath) {
        return uploadFile(key, new File(absoluteFilePath));
    }

    public static String uploadFile(String key, File file) {
        return uploadFile(Key.BUCKETNAME.val, key, file);
    }

    public static String uploadFile(String bucketName, String key, File file) {
        //TODO key 的合法性没有校验，比如 key = 文件名 + 后缀。但实际是没有后缀或者只有后缀的情况
        // 没有校验 file 是否存在
        // 没有校验 key 的后缀 是否与 file 本身的后缀匹配，比如 key 是图片格式，但 file 是文本格式，这样上传以后就会出现问题
        // 但如果都为同一类型，比如同为文本格式，一个为 .doc，一个为 .txt，这样是可以的；但是 一个为 docx， 一个是 txt，是不行的
        // 还是说必须要保证是同一种类型，即使是 doc 保存成 txt 也是不可以的
        return getClient().putObject(bucketName, key, file).getVersionId();
    }

    public static String uploadFile(String key, byte[] data) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        return getClient().putObject(Key.BUCKETNAME.val, key, new ByteArrayInputStream(data), metadata).getVersionId();
    }

    public static String uploadFile(String key, InputStream inputStream) {
        byte[] data = new byte[0];
        try {
            data = new byte[inputStream.available()];
            inputStream.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uploadFile(key, data);
    }

    public static String uploadFileOfficial(String key, File file) {
        PutObjectRequest request = new PutObjectRequest(Key.BUCKETNAME.val, key, file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("plain/text");
        metadata.addUserMetadata("x-amz-meta-title", "fileTitle");
        request.setMetadata(metadata);
        PutObjectResult putObjectResult = getClient().putObject(request);
        return putObjectResult.getVersionId();
    }

    public static void uploadFileOfficialHighLevel(String key, File file) {
        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(getClient())
                .build();
        Upload upload = tm.upload(Key.BUCKETNAME.val, key, file);
        try {
            System.out.println("线程：【" + Thread.currentThread().getName() + "】 开始执行");
            upload.waitForUploadResult();
        }
        // InterruptedException:The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.
        // SdkClientException:Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.
        catch (InterruptedException | SdkClientException e) {
            e.printStackTrace();
        }
        tm.shutdownNow();
    }

    public static void uploadFileTraceOfficialHighLevel(String key, File file) {
        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(getClient())
                .build();
        PutObjectRequest request = new PutObjectRequest(Key.BUCKETNAME.val, key, file);
        /*request.setGeneralProgressListener(progressEvent ->
                System.out.println(Thread.currentThread().getName() + ",Transferred bytes: " + progressEvent.getBytesTransferred()));*/

        Upload upload = tm.upload(request);
        try {
            upload.waitForUploadResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tm.shutdownNow();
    }

    /**
     * 上传文件夹
     *
     * @param directoryName
     * @param directoryPath
     * @param isIncludeSubdirectories
     */
    public  static void uploadDirectory(String directoryName, String directoryPath, boolean isIncludeSubdirectories) {
        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(getClient())
                .build();
        // TransferManager 采用异步方式进行处理，因此该调用会立即返回。
        MultipleFileUpload upload = tm.uploadDirectory(Key.BUCKETNAME.val, directoryName, new File(directoryPath), isIncludeSubdirectories);
        try {
            // 等待上传全部完成。
            upload.waitForCompletion();
        } catch (AmazonClientException amazonClientException) {
            amazonClientException.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //在完成操作后，您必须显示关闭TransferManager。
        tm.shutdownNow();
    }

    private static Result<String> buildSuccessResult() {
        return new Result<>(ResultStatusEnum.SUCCESS, ResultStatusEnum.getDescription(ResultStatusEnum.SUCCESS.getCode()), null);
    }

    public static S3ObjectInputStream getInputStreamFromS3(String key) {
        return getClient().getObject(Key.BUCKETNAME.val, key).getObjectContent();
    }

    public static S3ObjectInputStream getInputStreamFromS3(String key, String versionId) {
        return getInputStreamFromS3(Key.BUCKETNAME.val, key, versionId);
    }

    public static S3ObjectInputStream getInputStreamFromS3(String bucketName, String key, String versionId) {
        return getClient().getObject(new GetObjectRequest(bucketName, key, versionId)).getObjectContent();
    }

    public static String getUrlFromS3(String key) {
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(Key.BUCKETNAME.val, key);
        return String.valueOf(getClient().generatePresignedUrl(urlRequest));
    }

    private static ObjectMetadata getObject(GetObjectRequest objectRequest, String filePath) {
        return getClient().getObject(objectRequest, new File(filePath));
    }

    private static GetObjectRequest getObjectRequest(String key, String versionId) {
        return new GetObjectRequest(Key.BUCKETNAME.val, key, versionId);
    }

    private static GetObjectRequest getObjectRequest(String key) {
        return new GetObjectRequest(Key.BUCKETNAME.val, key);
    }

    private static ClientConfiguration config() {
        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(com.amazonaws.Protocol.HTTP);
        config.setSignerOverride("S3SignerType");
        return config;
    }

    private static void closeStream(Closeable in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static AmazonS3 getClient() {
//        return s3;
        return AmazonS3ClientBuilder.standard()
                .withClientConfiguration(config())
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(Key.ENDPOINT.val, ""))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(Key.ACCESSKEY.val, Key.SECRETKEY.val)))
                .build();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 30; i++) {
            new Thread(() -> StorageUtil.uploadFileTraceOfficialHighLevel("eclipse.zip", new File("d:/test/gradle-4.10.3-all.zip")), "Thread-" + i).start();
        }

        System.out.println("上传完成");
    }
}
