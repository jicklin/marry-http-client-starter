package com.marry.mhttp;

import com.marry.mhttp.config.HttpClientsRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author mal
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(HttpClientsRegister.class)
public @interface EnableSimpleHttpClients {

    /**
     * 默认扫描的路径
     * 比如 com.marry.clients
     * @return
     */
    String[] values() default {};

    /**
     * 默认扫描的路径
     * @return
     */
    String[] basePackages() default {};
}
