package me;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author: TangNan
 * @date: 2019-04-15 18:28
 */
public class Test
{
    public static void main(String[] args) throws IOException
    {
        // 指定需要登录的HCP 租户 及 桶
        String endpoint = "testcore.oss.foticit.com.cn";
        // 登录需要的用户名
        // The access key encoded by Base64
        String accessKey = "dGVzdGNvcmU=";
        // 登录需要的密码
        // The AWS secret access key encrypted by MD5
        String secretKey = "dddeabc0f5437949c0c46f694d3a7970";
        // 桶名称
        String bucketName = "testcore";
        String localDir = "d:/test/";
        String outputFileName = "test.txt";
        File file = new File(localDir + outputFileName);
        File inputFile = new File(localDir + "testFw.txt");

        com.amazonaws.ClientConfiguration clientConfig = new com.amazonaws.ClientConfiguration();
        clientConfig.setProtocol(com.amazonaws.Protocol.HTTP);
        clientConfig.setSignerOverride("S3SignerType");


        AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withClientConfiguration(clientConfig)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, ""))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        /*List<Bucket> list = client.listBuckets();
        list.forEach(bucket -> {
            System.out.println(bucket.toString());
            BucketVersioningConfiguration conf = client.getBucketVersioningConfiguration(bucketName);
            System.out.println("version config status:" + conf.getStatus());

            ListObjectsRequest request = new ListObjectsRequest();
            request.setBucketName(bucketName);
            client.listObjects(request);
            ObjectListing objectListing = client.listObjects(bucket.getName());
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries())
            {
                System.out.println(objectSummary.toString());

            }

        });*/
        // 上传文件
        PutObjectResult putObjectResult = client.putObject(bucketName, outputFileName, inputFile);
        // 根据上传返回信息，获取版本id
        String versionID = putObjectResult.getVersionId();
        System.out.println(versionID);
        versionID = "99544398310913";
        // 根据文件名 + 版本id 获取文件
        GetObjectRequest request = new GetObjectRequest(bucketName, outputFileName, versionID);
        ObjectMetadata meta = client.getObject(request, file);

    }
}
