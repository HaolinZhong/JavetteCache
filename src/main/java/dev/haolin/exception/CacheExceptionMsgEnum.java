package dev.haolin.exception;

public enum CacheExceptionMsgEnum {

    OVERFLOW_EXCEPTION("operation aborted because entry size is larger than the max cache size."),
    TOPIC_ALREADY_EXISTS("operation aborted because attempt to create a topic that already exists");

    CacheExceptionMsgEnum(String errMsg) {
        this.errMsg = errMsg;
    }

    private String errMsg;

    public String getErrMsg() {
        return errMsg;
    }

}
