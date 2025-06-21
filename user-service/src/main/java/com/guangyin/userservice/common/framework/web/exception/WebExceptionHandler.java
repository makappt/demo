package com.guangyin.userservice.common.framework.web.exception;

import com.guangyin.userservice.common.framework.core.exception.MicroServiceBusinessException;
import com.guangyin.userservice.common.framework.core.exception.MicroServiceFrameWorkException;
import com.guangyin.userservice.common.framework.core.response.ResponseCode;
import com.guangyin.userservice.common.framework.core.response.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理器
 *
 * NOTE RestController 注释的作用
 * 用于标识一个类作为 RESTful 风格的控制器，
 * RestController 包含了 @ResponseBody 的功能，方法返回的对象会自动被 Spring 转换为 JSON 或 XML 格式（默认是 JSON，依赖于 Jackson 库），
 * 并写入 HTTP 响应体中。无需手动添加 @ResponseBody 注解。
 *
 * NOTE RestControllerAdvice 注释的作用
 * 是一个用于处理全局异常和增强 RESTful 控制器的注解,结合了 @ControllerAdvice 和 @ResponseBody 的功能
 * 主要用于集中管理异常处理、返回统一的响应格式或对控制器进行全局增强。
 *
 * NOTE 通过 @ExceptionHandler 注解，可以捕获特定类型的异常并返回统一的响应格式（如 JSON）
 */
@RestControllerAdvice
public class WebExceptionHandler {
    /**
     * 处理自定义业务异常
     * 捕获 MiniPanBusinessException 类型的异常，通常用于业务逻辑中抛出的自定义错误。
     * @param e
     * @return
     */
    @ExceptionHandler(value = MicroServiceBusinessException.class)
    public Result MicroServiceBusinessExceptionHandler(MicroServiceBusinessException e)
    {
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 捕获 MethodArgumentNotValidException 类型的异常，通常在 @Valid 注解校验失败时抛出。
     * 提取第一个校验失败的错误信息
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e)
    {
        ObjectError objectError = e.getBindingResult().getAllErrors().stream().findFirst().get();
        return Result.fail(ResponseCode.ERROR_PARAM.getCode(),objectError.getDefaultMessage());
    }

    /**
     * 捕获 ConstraintViolationException 类型的异常，通常在 @Validated 注解校验路径参数或请求参数失败时抛出。
     * 提取第一个校验失败的错误信息，原因是使用了快速失败模式，当第一个校验失败时就会抛出异常。
     * @param e
     * @return
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public Result constraintDeclarationExceptionHandler(ConstraintViolationException e)
    {
        ConstraintViolation<?> constraintViolation = e.getConstraintViolations().stream().findFirst().get();
        return Result.fail(ResponseCode.ERROR_PARAM.getCode(),constraintViolation.getMessage());
    }

    /**
     *  捕获 MissingServletRequestParameterException 类型的异常，通常在请求中缺少必需参数时抛出。
     * @param e
     * @return
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public Result missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e)
    {
        return Result.fail(ResponseCode.ERROR_PARAM);
    }

    /**
     * 捕获 IllegalStateException 类型的异常，通常在程序运行时状态不合法时抛出。
     * @param e
     * @return
     */
    @ExceptionHandler(value = IllegalStateException.class)
    public Result illegalStateExceptionHandler(IllegalStateException e)
    {
        return Result.fail(ResponseCode.ERROR_PARAM);
    }

    /**
     * 捕获 BindException 类型的异常，通常在表单数据绑定到对象时校验失败时抛出。
     * 提取第一个字段校验失败的错误信息
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    public Result bindException(BindException e)
    {
        FieldError fieldError= e.getBindingResult().getFieldErrors().stream().findFirst().get();
        return Result.fail(ResponseCode.ERROR_PARAM.getCode(),fieldError.getDefaultMessage());
    }

    /**
     * 捕获 MiniPanFrameWorkException 类型的异常，通常用于框架内部抛出的自定义错误。
     * @param e
     * @return
     */
    @ExceptionHandler(value = MicroServiceFrameWorkException.class)
    public Result runtimeExceptionHandler(MicroServiceFrameWorkException e)
    {
        return Result.fail(ResponseCode.ERROR.getCode(),e.getMessage());
    }

    /**
     * 异常处理兜底
     * @param e
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    public Result runtimeExceptionHandler(RuntimeException e)
    {
        return Result.fail(ResponseCode.ERROR.getCode(),e.getMessage());
    }

}
