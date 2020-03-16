package support;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author: mfh
 * @Date: 2019-04-18 17:14
 **/
public class FileAssist {

    /**
     * 创建新文件
     * @param path
     * @return
     */
    public static File newFile(String path) {
        return new File(path);
    }

    /**
     * 断言指定路径下文件一定存在
     *
     * @param filePath
     */
    public static void fileMustBeExist(String filePath) {
        File file = new File(filePath);
        Assert.assertTrue(file.exists());
    }

    /**
     * 创建一个由当前时间戳命名的文件名
     *
     * @param suffix 后缀
     * @return
     */
    public static String createFileName(String suffix) {
        return createFileName() + "." + suffix;
    }

    /**
     * 文件下载到绝对路径下
     *
     * @param suffix
     * @return
     */
    public static String absolutePathWithDownLoad(String suffix) {
        return testFilePath() + createFileName(suffix);
    }

    /**
     * 获取一个肯定存在的文件
     *
     * @return
     */
    public static File getMustBeExistFile() {
        //TODO 这里应该通过程序找出 filePath() 文件夹下的任意一个文件，而不是硬编码的方式指定
        return newFile(filePath() + "city.txt");
    }

    /**
     * 获取一个肯定存在的图片
     * @return
     */
    public static File getMustBuExistFileJpg() {
        return newFile(filePath() + "sc.jpg");
    }

    /**
     * 获取一个肯定存在文件的绝对路径
     * @return
     */
    public static String getMustBeExistAbsoluteFilePath() {
        return filePath() + "city.txt";
    }

    public static void downloadFile(S3ObjectInputStream inputStream, String suffix) {
        byte[] data;
        FileOutputStream stream = null;
        try {
            data = new byte[inputStream.available()];
            stream = new FileOutputStream(FileAssist.newFile(testFilePath() + createFileName() + "." + suffix));
            int len;
            while ((len = inputStream.read(data)) != -1) {
                stream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private static String testFilePath() {
        return "d:/test/aws/";
    }

    private static String filePath() {
        return "d:/test/";
    }

    private static String createFileName() {
        return String.valueOf(System.currentTimeMillis());
    }
}
