package com.dolplay.nutzrc.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Conanca
 * 指明要进行缓存操作的方法的注解
 * 注:方法还需要声明@Aop("cacheInterceptor")注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface Cache {
	public CRUD oper() default CRUD.READ;

	public String cacheNamePrefix() default "";

	public String[] delCaches() default {""};
}
