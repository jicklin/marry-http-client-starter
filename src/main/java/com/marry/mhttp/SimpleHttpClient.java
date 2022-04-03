package com.marry.mhttp;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author mal
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SimpleHttpClient {

    @AliasFor("name")
    String value() default "";


    @AliasFor("value")
    String name() default "";
}
