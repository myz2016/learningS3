import com.amazonaws.SdkClientException;
import mfh.StorageUtil;
import org.junit.Assert;
import org.junit.Test;
import support.FileAssist;
import support.IllegalIdGenerator;

import java.io.*;

/**
 * @Author: mfh
 * @Date: 2019-04-17 13:20
 **/
public class S3StoreUploadTest {
    @Test
    public void testUploadFile() {
        {
            StorageUtil.uploadFile(FileAssist.createFileName("txt"), FileAssist.getMustBeExistAbsoluteFilePath());
        }
        {
            StorageUtil.uploadFile(FileAssist.createFileName("doc"), FileAssist.getMustBeExistAbsoluteFilePath());
        }
        {
            StorageUtil.uploadFile(FileAssist.getMustBeExistAbsoluteFilePath());
        }
    }

    @Test
    public void testUploadFileWithInputStream() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(FileAssist.getMustBeExistFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String fileName = FileAssist.createFileName("txt");
        String versionId = StorageUtil.uploadFile(fileName, inputStream);
        System.out.println("filename: " + fileName + ", versionId: " + versionId);
    }

    @Test
    public void testUploadFileWithByte() {
        byte[] data = null;
        try {
            FileInputStream inputStream = new FileInputStream(FileAssist.getMustBeExistFile());
            data = new byte[inputStream.available()];
            inputStream.read(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StorageUtil.uploadFile(FileAssist.createFileName("doc"), data);
    }

    @Test
    public void testUploadFileOfficial() {
        StorageUtil.uploadFileOfficial(FileAssist.createFileName("txt"), FileAssist.getMustBeExistFile());
        //文件类型与被测方法中的 contentType 不一致，可以正常上传
        StorageUtil.uploadFileOfficial(FileAssist.createFileName("txt"), FileAssist.getMustBuExistFileJpg());
    }

    @Test
    public void testUploadFileMismatchFileType() {
        /**
         * 本身上传不会有问题，但是上传到 s3 的图片文件，下载下来后肯定是损坏的，打不开的
         */
        String versionId = StorageUtil.uploadFile(FileAssist.createFileName("jpg"), FileAssist.getMustBeExistFile());
    }

    @Test
    public void testUploadFileWithNoneExistFilePath() {
        try {
            StorageUtil.uploadFile(FileAssist.createFileName("doc"), IllegalIdGenerator.noneExistFilePath());
        } catch (SdkClientException e) {
            return;
        }
        Assert.fail();
    }

    //    @Test
    public void testUploadFileOfficialHighLevel() {
        StorageUtil.uploadFileOfficialHighLevel(FileAssist.createFileName("txt"), FileAssist.getMustBeExistFile());
    }

    @Test
    public void testUploadLargeFileHighLevel() {
        StorageUtil.uploadFileOfficialHighLevel("eclipse.zip", new File("d:/test/gradle-4.10.3-all.zip"));
    }

    @Test
    public void testUploadLargeFileTraceHighLevel() {

    }

    @Test
    public void testUploadLoop() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
            StorageUtil.uploadFile(FileAssist.createFileName("doc"), FileAssist.getMustBeExistAbsoluteFilePath());
        }
        System.out.println(((System.currentTimeMillis() - start) / 1000) + " 秒");
    }

    public void testUploadDirectory() {
        StorageUtil.uploadDirectory("a", "d:/test/aws/", true);
    }
}
