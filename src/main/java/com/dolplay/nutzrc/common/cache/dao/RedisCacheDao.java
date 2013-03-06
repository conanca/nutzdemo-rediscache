package com.dolplay.nutzrc.common.cache.dao;

import java.util.HashSet;
import java.util.Set;

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
	 * @param cacheKey
	 * @param cacheValue
	 */
	public void set(String cacheKey, Object cacheValue) {
		int timeout = config.getInt("STANDARD_CACHE_TIMEOUT", CacheConfig.DEFAULT_STANDARD_CACHE_TIMEOUT);
		set(cacheKey, timeout, cacheValue);
	}

	/**
	 * 增加一个缓存，如果存在将更新该缓存。可指定缓存超时时间，如果超时时间小于等于0，则为永久缓存
	 * @param cacheKey
	 * @param timeout
	 * @param cacheValue
	 */
	public void set(String cacheKey, int timeout, Object cacheValue) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (timeout <= 0) {
				jedis.set(cacheKey, Json.toJson(cacheValue));
			} else {
				jedis.setex(cacheKey, timeout, Json.toJson(cacheValue));
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
	 * @param cacheKey
	 * @return
	 */
	public String get(String cacheKey) {
		String valueJson = null;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			valueJson = jedis.get(cacheKey);
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
	 * @param cacheKeys
	 * @return
	 */
	public long remove(String... cacheKeys) {
		Jedis jedis = null;
		Long count = null;
		try {
			jedis = jedisPool.getResource();
			count = jedis.del(cacheKeys);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
		return count == null ? 0 : count.longValue();
	}

	/**
	 * 为给定缓存设置超时时间，当缓存 过期时(超时时间为 0 )，它会被自动删除
	 * @param cacheKey
	 * @param seconds
	 * @return
	 */
	public boolean expire(String cacheKey, int seconds) {
		Jedis jedis = null;
		long success = 0;
		try {
			jedis = jedisPool.getResource();
			success = jedis.expire(cacheKey, seconds);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
		return success == 1 ? true : false;
	}

	@Override
	public boolean exists(String cacheKey) {
		Jedis jedis = null;
		boolean isExist = false;
		try {
			jedis = jedisPool.getResource();
			isExist = jedis.exists(cacheKey);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
		return isExist;
	}

	@Override
	public Set<String> keySet(String pattern) {
		Jedis jedis = null;
		Set<String> keySet = new HashSet<String>();
		try {
			jedis = jedisPool.getResource();
			keySet = jedis.keys(pattern);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
		return keySet;
	}
}
