package support;

/**
 * @Author: mfh
 * @Date: 2019-04-19 8:37
 **/
public class IllegalIdGenerator {
    /**
     * s3 服务器上不存在key（文件名 + 后缀）
     * @return
     */
    public static String noneExistKey() {
        return "illegalKey";
    }

    /**
     * 不存在的文件路径
     * @return
     */
    public static String noneExistFilePath() {
        return "f:/foo/";
    }

    /**
     * 非法的 versionId，versionId 应该由大于0的数字组成
     * @return
     */
    public static String invalidVersionId() {
        return "invalidVersionId";
    }

    /**
     * 不存在的桶名称
     * @return
     */
    public static String noneExistBucketName() {
        return "noneExistBucketName";
    }

    /**
     * 路径存在，但未指定保存的文件格式
     * @return
     */
    public static String notSpecifiedFileName() {
        return "d:/test/aws";
    }
}
