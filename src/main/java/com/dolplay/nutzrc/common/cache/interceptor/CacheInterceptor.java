package com.dolplay.nutzrc.common.cache.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dolplay.nutzrc.common.cache.CacheConfig;
import com.dolplay.nutzrc.common.cache.annotation.Cache;
import com.dolplay.nutzrc.common.cache.annotation.CacheNameSuffix;
import com.dolplay.nutzrc.common.cache.dao.CacheDao;

/**
 * @author Conanca
 *  实现缓存预先读取及缓存自动设值的方法拦截器
 */
public class CacheInterceptor implements MethodInterceptor {
	private static Logger logger = LoggerFactory.getLogger(CacheInterceptor.class);

	private CacheDao cacheDao;

	public void filter(InterceptorChain chain) throws Throwable {
		// 取得被拦截的方法及其注解
		Method method = chain.getCallingMethod();
		Cache cacheAn = method.getAnnotation(Cache.class);
		// 获取缓存KEY的前缀：cacheNamePrefix
		String cacheNamePrefix = cacheAn.cacheNamePrefix();
		// 若cacheNamePrefix不为空，将对该缓存做相应操作否则直接执行方法
		if (!Strings.isEmpty(cacheNamePrefix)) {
			// 获取该方法欲读取的缓存的 KEY——将拼接方法注解中的cacheNamePrefix及标有“CacheNameSuffix”注解的参数
			Object args[] = chain.getArgs();
			String[] cacheParaArr = new String[args.length];
			Annotation[][] ans = method.getParameterAnnotations();
			int k = 0;
			if (ans.length > 0) {
				for (int i = 0; i < ans.length; i++) {
					for (int j = 0; j < ans[i].length; j++) {
						if (ans[i][j].annotationType() == CacheNameSuffix.class) {
							cacheParaArr[k] = Json.toJson(args[i]);
							k++;
						}
					}
				}
			}
			StringBuilder cacheNameSb = new StringBuilder(cacheNamePrefix);
			for (String cacahePara : cacheParaArr) {
				if (cacahePara != null) {
					cacheNameSb.append(CacheConfig.CACHENAME_DELIMITER);
					cacheNameSb.append(cacahePara);
				}
			}
			String cacheName = cacheNameSb.toString();
			logger.debug("Cache name : " + cacheName);

			// 获取该方法欲读取的缓存的 VALUE
			String cacheValue = null;
			try {
				cacheValue = cacheDao.get(cacheName);
			} catch (Exception e) {
				logger.error("Read Cache error", e);
			}
			// 若缓存值不为空，则该方法直接返回缓存里相应的值
			if (cacheValue != null) {
				chain.setReturnValue(Json.fromJson(method.getReturnType(), cacheValue));
				logger.debug("Get a value from this cache");
				return;
			} else {
				logger.debug("Can't get any value from this cache");
			}
			// 执行方法
			chain.doChain();
			// 获取方法返回值并增加相应缓存
			Object returnObj = chain.getReturn();
			if (returnObj != null) {
				try {
					cacheDao.set(cacheName, returnObj);
					logger.debug("Set a new value for this cache");
				} catch (Exception e) {
					logger.error("Set cache error", e);
				}
			} else {
				logger.warn("No value to set for this cache");
			}
		} else {
			// 执行方法
			chain.doChain();
		}
	}
}
