package com.guangyin.userservice.common.framework.web.web.validator;

import com.guangyin.userservice.common.framework.core.constants.MicroServiceConstants;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * 统一的参数校验器配置类，面向切面编程
 * 该类用于配置 Hibernate Validator 作为参数校验工具，并通过 Spring 的 MethodValidationPostProcessor,一个 Spring AOP（面向切面编程）后处理器，
 * 启用方法级别的参数校验（如 Controller 方法参数上的 @Valid、@NotNull 等注解）。
 *
 *当 Spring Boot 应用启动时，Spring 容器会扫描所有标注了 @SpringBootConfiguration 或 @Configuration 的配置类。
 * WebValidatorConfig 类被识别为一个配置类，Spring 会处理其中的 @Bean 方法，创建并注册相应的 Bean。
 *
 * Spring 调用 methodValidationPostProcessor() 方法，创建一个 MethodValidationPostProcessor 实例。
 * 在该方法中，调用 miniPanValidator() 方法，构建一个基于 Hibernate Validator 的 Validator 实例。
 * 将该 Validator 实例设置到 MethodValidationPostProcessor 中。MethodValidationPostProcessor Bean 被注册到 Spring 容器中。
 *
 * NOTE 注释校验的流程
 * 当一个 HTTP 请求到达 Spring MVC 的 Controller 方法时，如果方法参数或返回值上标注了校验注解（如 @Valid、@NotNull），Spring 会触发MethodValidationPostProcessor校验逻辑。
 *  MethodValidationPostProcessor 是一个 Spring AOP（面向切面编程）后处理器，它会在方法执行前后拦截方法调用。
 * 在方法执行前，MethodValidationPostProcessor 会检查方法参数是否需要校验（基于注解）。
 * 如果需要校验，它会调用之前配置的 Validator 实例（即 miniPanValidator() 返回的 Hibernate Validator）对参数进行校验。
 * 如果校验失败，Spring 会抛出异常（如 MethodArgumentNotValidException），通常会被全局异常处理器捕获并返回错误响应给客户端。
 * 如果校验通过，Controller 方法正常处理请求并返回结果。
 *
 * NOTE 注释校验的使用方法：
 * 对于标注了 @Validated 注解的类或方法，它会创建一个代理对象，当方法被调用时，代理对象会先执行校验逻辑（调用 Validator），再决定是否执行目标方法。
 * 即HTTP 请求到达 Controller，MethodValidationPostProcessor 拦截方法调用，使用 Validator 对参数进行校验。
 * 校验通常在方法执行前进行（前置通知）。
 * 如果校验失败，方法不会被执行，直接抛出异常。
 */
@SpringBootConfiguration
@Log4j2
public class WebValidatorConfig {

    // 定义 Hibernate Validator 的快速失败属性键，用于配置校验行为
    private static final String FAIL_FAST_KEY = "hibernate.validator.fail_fast";

    /**
     * 配置 MethodValidationPostProcessor Bean，用于支持方法级别的参数校验
     * 该 Bean 会拦截方法调用，自动对方法参数进行校验。
     *
     * @return MethodValidationPostProcessor 实例，配置了自定义的 Validator
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(miniPanValidator());
        log.info("The hibernate validator is loaded successfully!");
        return postProcessor;
    }

    /**
     * 构造项目的方法参数校验器
     * 该方法创建一个基于 Hibernate Validator 的 Validator 实例，并配置快速失败模式。
     * 快速失败模式下，校验遇到第一个错误时立即返回，不继续校验其他字段。
     *
     * @return Validator 实例，用于实际的参数校验
     */
    private Validator miniPanValidator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty(FAIL_FAST_KEY, MicroServiceConstants.TRUE_STR)// 设置快速失败模式，值为 "true"
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        return validator;
    }


}
