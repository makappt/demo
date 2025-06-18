package com.guangyin.core.exception;

import com.guangyin.core.response.ResponseCode;
import lombok.Data;

/**
 * 自定义异常类，用于处理业务异常
 */
@Data
public class MicroServiceBusinessException extends RuntimeException {
    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    public MicroServiceBusinessException(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getDesc();
    }

    public MicroServiceBusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public MicroServiceBusinessException(String message) {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = message;
    }

    public MicroServiceBusinessException() {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = ResponseCode.ERROR_PARAM.getDesc();
    }
}
