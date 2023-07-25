package com.zwj.client.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/5 15:42
 **/
@Target({ElementType.CONSTRUCTOR,ElementType.ANNOTATION_TYPE,ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Autowired
public @interface RpcAutowired {
    String version() default "1.0";
}
