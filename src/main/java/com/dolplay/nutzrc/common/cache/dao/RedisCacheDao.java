package com.dolplay.nutzrc.common.cache.dao;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.dolplay.nutzrc.common.cache.CacheConfig;

public class RedisCacheDao implements CacheDao {
	private static Logger logger = LoggerFactory.getLogger(RedisCacheDao.class);

	private PropertiesProxy config;
	private JedisPool jedisPool;

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
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.setex(cacheName, config.getInt("CACHE_TIMEOUT", CacheConfig.DEFAULT_CACHE_TIMEOUT),
					Json.toJson(cacheValue));
		} catch (Exception e) {
			logger.error("Redis Error", e);
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}

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
			if (timeout <= 0) {
				jedis.set(cacheName, Json.toJson(cacheValue));
			} else {
				jedis.setex(cacheName, timeout, Json.toJson(cacheValue));
			}
		} catch (Exception e) {
			logger.error("Redis Error", e);
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
			valueJson = jedis.get(cacheName);
		} catch (Exception e) {
			logger.error("Redis Error", e);
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
			count = jedis.del(cacheNames);
		} catch (Exception e) {
			logger.error("Redis Error", e);
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
		return count == null ? 0 : count.longValue();
	}
}
