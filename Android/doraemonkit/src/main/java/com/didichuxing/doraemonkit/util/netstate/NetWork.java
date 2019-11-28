package com.didichuxing.doraemonkit.util.netstate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author maple on 2019/7/9 17:05.
 * @version v1.0
 * @see 1040441325@qq.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NetWork {
    NetType value() default NetType.AUTO;
}
