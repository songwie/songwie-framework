package com.songwie.concurrent.lock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //设定此注解对应的是方法。
//不能嵌套调用
public @interface DistributedLockAnnotation {
	int timeout() default 10;
	String key();
}
