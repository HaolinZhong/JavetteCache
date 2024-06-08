package dev.haolin.exception;

public class CacheException extends RuntimeException{
    public CacheException(CacheExceptionMsgEnum cacheExceptionMsgEnum) {
        super(cacheExceptionMsgEnum.getErrMsg());
    }
}
