package org.jeecg.modules.bill.exception;

/**
 * @author dbc
 * @create 2023-05-31 19:07
 */
public class ByxException  extends RuntimeException{
    private int code = 500;
    private String msg = "服务器异常";


    public ByxException(ExceptionCodeMsg appExceptionCodeMsg){
        super();
        this.code = appExceptionCodeMsg.getCode();
        this.msg = appExceptionCodeMsg.getMsg();

    }

    public ByxException(int code,String msg){
        super();
        this.code = code;
        this.msg = msg;

    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
