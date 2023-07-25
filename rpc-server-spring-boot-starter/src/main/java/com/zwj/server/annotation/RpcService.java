package com.zwj.server.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/5 14:32
 **/
//ElementType.TYPE表示该注解可以应用于类、接口或枚举类型
@Target({ElementType.TYPE})
@Documented
//@Retention注解用于指定注解本身的保留策略。RetentionPolicy.RUNTIME表示该注解应在运行时保留，可以使用反射在程序执行期间进行访问和处理。
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface RpcService {
    //暴露服务接口的类型默认是Object
    Class<?> interfaceType() default Object.class;
    //服务版本默认是1.0
    String version() default "1.0";
}
