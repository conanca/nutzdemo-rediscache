package com.dolplay.nutzrc.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface Cache {
	public CRUD oper() default CRUD.READ;

	public String cacheNamePrefix() default "";

	public String[] delCaches() default "";
}
