import com.amazonaws.services.s3.model.S3ObjectInputStream;
import mfh.StorageUtil;
import org.junit.Test;
import support.FileAssist;

/**
 * @Author: mfh
 * @Date: 2019-04-25 10:44
 **/
public class S3StoreNormalTest {
    @Test
    public void testGetInputStreamFromS3() {
        String key = "test.txt";
        {

            S3ObjectInputStream inputStream = StorageUtil.getInputStreamFromS3(key);
            FileAssist.downloadFile(inputStream, "txt");
        }
        {
            String versionId = "99545610198209";
            S3ObjectInputStream inputStream = StorageUtil.getInputStreamFromS3(key, versionId);
            FileAssist.downloadFile(inputStream, "txt");
        }

    }

    @Test
    public void testGetUrlFromS3() {
        String key = "test.txt";
        String url = StorageUtil.getUrlFromS3(key);
        // http://testcore.testcore.oss.foticit.com.cn/test.txt?AWSAccessKeyId=dGVzdGNvcmU%3D&Expires=1555897179&Signature=ZOeTa3RJFpf5Z7iXTEQptO3%2FasU%
        System.out.println(url);
    }
}
