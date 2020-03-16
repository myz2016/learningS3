package mfh;

/**
 * @author : mfh
 * @date : 2019-08-09 16:25
 **/
public enum ResultStatusEnum {
    /**
     * 成功
     */
    SUCCESS(1, "成功"),
    /**
     * 失败
     */
    FAILURE(0, "失败"),
    /**
     * 非法参数
     */
    ILLEGAL_PARAMETER(-99, "非法参数"),
    ;
    private int code;
    private String description;

    ResultStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public static String getDescription(int code) {
        for (ResultStatusEnum eachState : values()) {
            if (code == eachState.code) {
                return eachState.description;
            }
        }
        return ILLEGAL_PARAMETER.description;
    }
}