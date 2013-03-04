package com.dolplay.nutzrc.common.cache.dao;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.dolplay.nutzrc.common.cache.CacheConfig;

/**
 * Redis实现的缓存DAO
 * @author conanca
 *
 */
public class RedisCacheDao implements CacheDao {
	protected PropertiesProxy config;
	protected JedisPool jedisPool;

	public RedisCacheDao(PropertiesProxy config, JedisPool jedisPool) {
		this.config = config;
		this.jedisPool = jedisPool;
	}

	/**
	 * 增加一个缓存，如果存在将更新该缓存。注：缓存超时时间由配置文件配置
	 * @param cacheName
	 * @param cacheValue
	 */
	public void set(String cacheName, Object cacheValue) {
		int timeout = config.getInt("STANDARD_CACHE_TIMEOUT", CacheConfig.DEFAULT_STANDARD_CACHE_TIMEOUT);
		set(cacheName, timeout, cacheValue);
	}

	/**
	 * 增加一个缓存，如果存在将更新该缓存。可指定缓存超时时间，如果超时时间小于等于0，则为永久缓存
	 * @param cacheName
	 * @param timeout
	 * @param cacheValue
	 */
	public void set(String cacheName, int timeout, Object cacheValue) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.select(config.getInt("STANDARD_CACHE_REDIS_DATABASE_INDEX",
					CacheConfig.DEFAULT_STANDARD_CACHE_REDIS_DATABASE_INDEX));
			if (timeout <= 0) {
				jedis.set(cacheName, Json.toJson(cacheValue));
			} else {
				jedis.setex(cacheName, timeout, Json.toJson(cacheValue));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 根据缓存名获取一个缓存的值
	 * @param cacheName
	 * @return
	 */
	public String get(String cacheName) {
		String valueJson = null;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.select(config.getInt("STANDARD_CACHE_REDIS_DATABASE_INDEX",
					CacheConfig.DEFAULT_STANDARD_CACHE_REDIS_DATABASE_INDEX));
			valueJson = jedis.get(cacheName);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
		return valueJson;
	}

	/**
	 * 删除一个或多个缓存。
	 * @param cacheNames
	 * @return
	 */
	public long remove(String... cacheNames) {
		Jedis jedis = null;
		Long count = null;
		try {
			jedis = jedisPool.getResource();
			jedis.select(config.getInt("STANDARD_CACHE_REDIS_DATABASE_INDEX",
					CacheConfig.DEFAULT_STANDARD_CACHE_REDIS_DATABASE_INDEX));
			count = jedis.del(cacheNames);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
		return count == null ? 0 : count.longValue();
	}

	/**
	 * 为给定缓存设置生存时间，当缓存 过期时(生存时间为 0 )，它会被自动删除
	 * @param cacheName
	 * @param seconds
	 * @return
	 */
	public boolean expire(String cacheName, int seconds) {
		Jedis jedis = null;
		long success = 0;
		try {
			jedis = jedisPool.getResource();
			jedis.select(config.getInt("STANDARD_CACHE_REDIS_DATABASE_INDEX",
					CacheConfig.DEFAULT_STANDARD_CACHE_REDIS_DATABASE_INDEX));
			success = jedis.expire(cacheName, seconds);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
		return success == 1 ? true : false;
	}
}
