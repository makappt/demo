package com.guangyin.userservice.common.framework.core.exception;

/**
 * 组件层面的异常
 */
public class MicroServiceFrameWorkException extends RuntimeException{
    public MicroServiceFrameWorkException(String msg) {
        super(msg);
    }
}
