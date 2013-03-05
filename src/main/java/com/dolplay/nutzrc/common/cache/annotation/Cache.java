package com.dolplay.nutzrc.common.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dolplay.nutzrc.common.cache.CacheType;

/**
 * @author Conanca
 * 指明要进行缓存操作的方法的注解
 * 注:方法还需要声明@Aop("cacheInterceptor")注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface Cache {

	/**
	 * 指明预读取/设置哪个缓存
	 * @return
	 */
	public String cacheNamePrefix() default "";

	/**
	 * 指明缓存类型。根据类型不同，可缓存字符串(任何对象均可被缓存，都将被转为JSON格式的字符串)值和有序集合值
	 * @return
	 */
	public CacheType cacheType() default CacheType.String;

	/**
	 * 指明缓存超时时间，超过这个时间该缓存将被删除。
	 * 缺省则使用配置文件中的相应配置
	 * @return
	 */
	public int cacheTimeout() default -1;

}