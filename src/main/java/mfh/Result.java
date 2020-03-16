package mfh;

import java.util.Objects;

/**
 * @author : mfh
 * @date : 2019-08-09 16:08
 **/
public class Result<T> {
    private ResultStatusEnum code;
    private String url;
    private String msg;
    private Exception ex;
    private T detail;

    private Result() {}

    public Result(ResultStatusEnum code, String msg, Exception ex) {
        this.code = code;
        this.msg = msg;
        this.ex = ex;
    }

    public Result(ResultStatusEnum code, String url, String msg, Exception ex, T detail) {
        this.code = code;
        this.url = url;
        this.msg = msg;
        this.ex = ex;
        this.detail = detail;
    }

    public ResultStatusEnum getCode() {
        return code;
    }

    public String getUrl() {
        return url;
    }

    public String getMsg() {
        return msg;
    }

    public T getDetail() {
        return detail;
    }

    public void setCode(ResultStatusEnum code) {
        this.code = code;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setEx(Exception ex) {
        this.ex = ex;
    }

    public void setDetail(T detail) {
        this.detail = detail;
    }

    public void printStackTrace() {
        if (Objects.nonNull(ex)) {
            ex.printStackTrace();
        }
    }

    public String getMessage() {
        if (Objects.nonNull(ex)) {
            return ex.getMessage();
        }
        return "无异常信息";
    }

    /**
     * 是否成功
     * @return 成功:true;失败:false
     */
    public boolean isSuccess() {
        return ResultStatusEnum.SUCCESS.getCode() == this.code.getCode();
    }
}

