package com.guangyin.core.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * 通用返回结果类，用于封装API响应数据。
 * 使用 @JsonInclude 注解控制序列化行为，属性值为 null 时不包含在 JSON 输出中。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor(force = true)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;

    /**
     * 响应数据
     */
    private final T data;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private Result(Integer code, String message) {
        this(code, message, null);
    }

    private Result(Integer code) {
        this(code, null, null);
    }

    /**
     * 判断请求是否成功。
     * 使用 @JsonIgnore 和 @JSONField 注解确保该方法不会被序列化到 JSON 中。
     *
     * @return 是否成功
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isSuccess() {
        return Objects.equals(this.code, ResponseCode.SUCCESS.getCode());
    }

    /**
     * 返回成功结果，默认状态码为 SUCCESS。
     *
     * @param <T> 泛型类型
     * @return 成功结果
     */
    public static <T> Result<T> success() {
        return new Result<>(ResponseCode.SUCCESS.getCode());
    }

    /**
     * 返回成功结果，包含自定义消息。
     *
     * @param message 自定义响应消息
     * @param <T>     泛型类型
     * @return 成功结果
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(ResponseCode.SUCCESS.getCode(), message);
    }

    /**
     * 返回成功结果，包含自定义数据。
     *
     * @param data 自定义响应数据
     * @param <T>  泛型类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDesc(), data);
    }

    /**
     * 返回失败结果，默认状态码为 ERROR。
     *
     * @param <T> 泛型类型
     * @return 失败结果
     */
    public static <T> Result<T> fail() {
        return new Result<>(ResponseCode.ERROR.getCode());
    }

    /**
     * 返回失败结果，包含自定义消息。
     *
     * @param message 自定义响应消息
     * @param <T>     泛型类型
     * @return 失败结果
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(ResponseCode.ERROR.getCode(), message);
    }

    /**
     * 返回失败结果，包含自定义状态码和消息。
     *
     * @param code    自定义状态码
     * @param message 自定义响应消息
     * @param <T>     泛型类型
     * @return 失败结果
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 返回失败结果，基于指定的响应码枚举。
     *
     * @param responseCode 响应码枚举
     * @param <T>          泛型类型
     * @return 失败结果
     */
    public static <T> Result<T> fail(ResponseCode responseCode) {
        return new Result<>(responseCode.getCode(), responseCode.getDesc());
    }
}
