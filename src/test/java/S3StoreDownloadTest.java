import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.transfer.exception.FileLockException;
import mfh.StorageUtil;
import org.junit.Assert;
import org.junit.Test;
import support.FileAssist;
import support.IllegalIdGenerator;
import support.Key;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * @Author: mfh
 * @Date: 2019-04-25 10:41
 **/
public class S3StoreDownloadTest {
    private HttpServletResponse response;
    @Test
    public void testDownFileWithNoneExistKey() {
        try {
            StorageUtil.downloadFile(IllegalIdGenerator.noneExistKey(), FileAssist.absolutePathWithDownLoad(".txt"));
        } catch (AmazonS3Exception e) {
            return;
        }
        Assert.fail();
    }

    @Test
    public void testDownFileWithNoneExistFilePath() {
        String key = "test.txt";
        try {
            StorageUtil.downloadFile(key, IllegalIdGenerator.noneExistFilePath());
        } catch (SdkClientException e) {
            //TODO 非法文件路径会抛出此异常，对此异常暂未做处理
            return;
        }
        Assert.fail();
    }

    @Test
    public void testDownFileWithNotSpecifiedFileName() {
        String key = "test.txt";
        try {
            StorageUtil.downloadFile(key, IllegalIdGenerator.notSpecifiedFileName());
        } catch (FileLockException e) {
            return;
        }
        Assert.fail();
    }

    @Test
    public void testDownFileMismatchKeyAndVersionId() {
        String key = "test.txt";
        // versionId 与 key 不匹配
        String versionId = "98231223";
        try {
            StorageUtil.downloadFile(
                    key,
                    versionId,
                    FileAssist.absolutePathWithDownLoad("doc"));
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
            return;
        }
        Assert.fail();
    }

    @Test
    public void testDownFileWithInvalidVersionId() {
        String key = "test.txt";
        try {
            StorageUtil.downloadFile(
                    key,
                    IllegalIdGenerator.invalidVersionId(),
                    FileAssist.absolutePathWithDownLoad("doc"));
        } catch (AmazonS3Exception e) {
            //Invalid version ID provided, must be an integer greater than 0
            return;
        }
        Assert.fail();
    }

    @Test
    public void testDownFileWithInvalidBucketName() {
        String key = "hd.txt";
        String versionId = "99550797571649";
        try {
            StorageUtil.downloadFile(
                    IllegalIdGenerator.noneExistBucketName(),
                    key,
                    versionId,
                    FileAssist.absolutePathWithDownLoad("doc"));
        } catch (AmazonS3Exception e) {
            // The specified bucket does not fileMustBeExist
            // NoSuchBucket
            return;
        }
        Assert.fail();
    }


    @Test
    public void testDownFile() {

        try {
            {
                String key = "hello.txt";
                String filePath = FileAssist.absolutePathWithDownLoad("doc");
                File file = new File(filePath);
                StorageUtil.downloadFile(key, filePath);
                Assert.assertTrue(file.exists());
            }

            {
                String key = "hd.txt";
                String filePath = FileAssist.absolutePathWithDownLoad("doc");
                String versionId = "99550797571649";
                File file = new File(filePath);
                StorageUtil.downloadFile(key, versionId, filePath);
                Assert.assertTrue(file.exists());
            }

            {
                String key = "hd.txt";
                String versionId = "99550797571649";
                String filePath = FileAssist.absolutePathWithDownLoad("doc");
                File file = new File(filePath);
                StorageUtil.downloadFile(Key.BUCKETNAME.val, key, versionId, filePath);
                Assert.assertTrue(file.exists());
            }

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * 测试不好
     */
//    @Test
    public void testDownFileWithResponse() {
        String key = "test.txt";
        String filePath = FileAssist.absolutePathWithDownLoad("doc");
        StorageUtil.downloadFile(key, filePath, response);
        FileAssist.fileMustBeExist(filePath);
    }

    @Test
    public void testDownFileBuffer() {
        String key = "test.txt";
        String filePath = FileAssist.absolutePathWithDownLoad("doc");
        StorageUtil.downloadFileBuffer(key, filePath);
        FileAssist.fileMustBeExist(filePath);
    }

    //    @Test
    public void testDownDirectory() {
        StorageUtil.downloadDirectory("dirToUpLoad", "d:/text/aws");
    }

    //    @Test
    public void testDownFileFromDirectory() {
        StorageUtil.downloadDirectory("dirToUpLoad/1555896130655.txt", "d:/text/aws");
    }

}
