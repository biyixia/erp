package org.jeecg.modules.bill.exception;

/**
 * @author dbc
 * @create 2023-05-31 19:05
 */
public enum ExceptionCodeMsg {
    INVENTORY_UNDERSTOCK(10000, "库存不足！"),
    INVALID_AUDIT(10001,"通过审核后无法修改");
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    ExceptionCodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
