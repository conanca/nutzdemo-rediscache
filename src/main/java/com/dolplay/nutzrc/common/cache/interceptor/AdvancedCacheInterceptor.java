package com.dolplay.nutzrc.common.cache.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dolplay.nutzrc.common.cache.CStrings;
import com.dolplay.nutzrc.common.cache.CacheType;
import com.dolplay.nutzrc.common.cache.Order;
import com.dolplay.nutzrc.common.cache.annotation.Cache;
import com.dolplay.nutzrc.common.cache.annotation.CacheNameSuffix;
import com.dolplay.nutzrc.common.cache.dao.AdvancedCacheDao;

/**
 * @author Conanca
 * 实现缓存预先读取及缓存自动设值的方法拦截器，支持普通类型缓存和有序集型缓存(注意：有序集缓存都是永久缓存)
 */
public class AdvancedCacheInterceptor implements MethodInterceptor {
	private static Logger logger = LoggerFactory.getLogger(AdvancedCacheInterceptor.class);

	private AdvancedCacheDao cacheDao;

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
			String cacheName = CStrings.cacheName(cacheNamePrefix, cacheParaArr);
			logger.debug("Cache name : " + cacheName);

			// 获取缓存类型，根据缓存类型不同分别对缓存有不同的操作方式
			CacheType cacheType = cacheAn.cacheType();
			if (cacheType.equals(CacheType.String)) {
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
			} else if (cacheType.equals(CacheType.List)) {
				// 获取该方法欲读取的缓存的 VALUE
				List<String> cacheValue = null;
				try {
					cacheValue = cacheDao.zQueryByRank(cacheName, 0, -1, Order.Asc);
				} catch (Exception e) {
					logger.error("Read Cache error", e);
				}
				// 若缓存值不为空，则该方法直接返回缓存里相应的值
				if (cacheValue != null && cacheValue.size() > 0) {
					chain.setReturnValue(cacheValue);
					logger.debug("Get a value from this cache");
					return;
				} else {
					logger.debug("Can't get any value from this cache");
				}
				// 执行方法
				chain.doChain();
				// 获取方法返回值并增加相应缓存
				@SuppressWarnings("unchecked")
				List<String> returnObj = (List<String>) chain.getReturn();
				if (returnObj != null) {
					try {
						for (String item : returnObj) {
							cacheDao.zAdd(cacheName, (new Date()).getTime(), item);
						}
						logger.debug("Set a new value for this cache");
					} catch (Exception e) {
						logger.error("Set cache error", e);
					}
				} else {
					logger.warn("No value to set for this cache");
				}
			} else {
				logger.error("The method annotation : CacheType Error!", new RuntimeException(
						"The method annotation : CacheType Error"));
			}
		} else {
			// 执行方法
			chain.doChain();
		}
	}
}
