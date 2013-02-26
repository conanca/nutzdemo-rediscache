package com.dolplay.nutzrc.cache;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.dolplay.nutzrc.util.IocProvider;

/**
 * 操作缓存的帮助类
 * @author conanca
 */
public class CacheHelper {

	/**
	 * 删除一个或多个缓存。
	 * @param cacheNames
	 * @return
	 */
	public static long remove(String... cacheNames) {
		JedisPool jedisPool = IocProvider.ioc().get(JedisPool.class, "jedisPool");
		Jedis jedis = jedisPool.getResource();
		Long count = jedis.del(cacheNames);
		jedisPool.returnResource(jedis);
		return count == null ? 0 : count.longValue();
	}

	/**
	 * 增加一个缓存，如果存在将更新该缓存。注：缓存超时时间由配置文件配置
	 * @param cacheName
	 * @param cacheValue
	 */
	public static void set(String cacheName, Object cacheValue) {
		PropertiesProxy config = IocProvider.ioc().get(PropertiesProxy.class, "config");
		JedisPool jedisPool = IocProvider.ioc().get(JedisPool.class, "jedisPool");
		Jedis jedis = jedisPool.getResource();
		jedis.setex(cacheName, config.getInt("CACHE_TIMEOUT", CacheConfig.DEFAULT_CACHE_TIMEOUT),
				Json.toJson(cacheValue));
		jedisPool.returnResource(jedis);
	}

	/**
	 * 增加一个缓存，如果存在将更新该缓存。可指定缓存超时时间，如果超时时间小于等于0，则为永久缓存
	 * @param cacheName
	 * @param timeout
	 * @param cacheValue
	 */
	public static void set(String cacheName, int timeout, Object cacheValue) {
		JedisPool jedisPool = IocProvider.ioc().get(JedisPool.class, "jedisPool");
		Jedis jedis = jedisPool.getResource();
		if (timeout <= 0) {
			jedis.set(cacheName, Json.toJson(cacheValue));
		} else {
			jedis.setex(cacheName, timeout, Json.toJson(cacheValue));
		}
		jedisPool.returnResource(jedis);
	}

	/**
	 * 根据缓存名获取一个缓存的值
	 * @param cacheName
	 * @return
	 */
	public static String get(String cacheName) {
		JedisPool jedisPool = IocProvider.ioc().get(JedisPool.class, "jedisPool");
		Jedis jedis = jedisPool.getResource();
		String valueJson = jedis.get(cacheName);
		jedisPool.returnResource(jedis);
		return valueJson;
	}
}
