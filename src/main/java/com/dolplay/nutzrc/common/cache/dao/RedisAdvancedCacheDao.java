package com.dolplay.nutzrc.common.cache.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.dolplay.nutzrc.common.cache.CacheConfig;
import com.dolplay.nutzrc.common.cache.Order;

/**
 * Redis实现的高级缓存DAO
 * @author conanca
 *
 */
public class RedisAdvancedCacheDao extends RedisCacheDao implements AdvancedCacheDao {

	public RedisAdvancedCacheDao(PropertiesProxy config, JedisPool jedisPool) {
		super(config, jedisPool);
	}

	/**
	 * 为有序集缓存的值增添一个成员，需指定该成员的score。
	 * 如果缓存不存在则创建这个缓存，并指定缓存超时时间；如果超时时间小于等于0，则为永久缓存
	 * @param cacheName
	 * @param seconds
	 * @param score
	 * @param item
	 */
	public void zAdd(String cacheName, int seconds, double score, String item) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.select(config.getInt("LIST_CACHE_REDIS_DATABASE_INDEX",
					CacheConfig.DEFAULT_LIST_CACHE_REDIS_DATABASE_INDEX));
			boolean isNew = !jedis.exists(cacheName);
			jedis.zadd(cacheName, score, item);
			if (isNew && seconds > 0) {
				jedis.expire(cacheName, seconds);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 为有序集缓存的值增添一个成员，需指定该成员的score。
	 * 如果缓存不存在则创建这个缓存，注：缓存超时时间由配置文件配置
	 * @param cacheName
	 * @param score
	 * @param item
	 */
	public void zAdd(String cacheName, double score, String item) {
		int timeout = config.getInt("LIST_CACHE_TIMEOUT", CacheConfig.DEFAULT_LIST_CACHE_TIMEOUT);
		zAdd(cacheName, timeout, score, item);
	}

	/**
	 * 查询有序集缓存，按照区间及排序方式
	 * 如：startIndex=0 endIndex=9 order=Order.Desc，按倒序取第1条-第10条
	 * @param cacheName
	 * @param key
	 * @param startIndex
	 * @param endIndex
	 * @param order
	 * @return
	 */
	public List<String> zQueryByRank(String cacheName, long startIndex, long endIndex, Order order) {
		Jedis jedis = null;
		List<String> valueList = null;
		try {
			jedis = jedisPool.getResource();
			jedis.select(config.getInt("LIST_CACHE_REDIS_DATABASE_INDEX",
					CacheConfig.DEFAULT_LIST_CACHE_REDIS_DATABASE_INDEX));
			Set<String> valueSet = null;
			if (order.equals(Order.Asc)) {
				valueSet = jedis.zrange(cacheName, startIndex, endIndex);
			} else {
				valueSet = jedis.zrevrange(cacheName, startIndex, endIndex);
			}
			valueList = new ArrayList<String>();
			valueList.addAll(valueSet);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
		return valueList;
	}

	/**
	 * 查询有序集缓存，按照score值范围及排序方式
	 * minScore=1997 maxScore=2013 order=Order.Desc，按倒序取score值在1997-2013的
	 * @param cacheName
	 * @param key
	 * @param minScore
	 * @param maxScore
	 * @param order
	 * @return
	 */
	public List<String> zQueryByScore(String cacheName, double minScore, double maxScore, Order order) {
		Jedis jedis = null;
		List<String> valueList = null;
		try {
			jedis = jedisPool.getResource();
			jedis.select(config.getInt("LIST_CACHE_REDIS_DATABASE_INDEX",
					CacheConfig.DEFAULT_LIST_CACHE_REDIS_DATABASE_INDEX));
			Set<String> valueSet = null;
			if (order.equals(Order.Asc)) {
				valueSet = jedis.zrangeByScore(cacheName, minScore, maxScore);
			} else {
				valueSet = jedis.zrevrangeByScore(cacheName, minScore, maxScore);
			}
			valueList = new ArrayList<String>();
			valueList.addAll(valueSet);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
		return valueList;
	}

	/**
	 * 删除有序集缓存的一部分成员，按照成员的值
	 * @param cacheName
	 * @param items
	 */
	public void zDel(String cacheName, String... items) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.select(config.getInt("LIST_CACHE_REDIS_DATABASE_INDEX",
					CacheConfig.DEFAULT_LIST_CACHE_REDIS_DATABASE_INDEX));
			jedis.zrem(cacheName, items);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 删除有序集缓存的一部分成员，按照区间
	 * @param cacheName
	 * @param startIndex
	 * @param endIndex
	 */
	public void zDelByRank(String cacheName, long startIndex, long endIndex) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.select(config.getInt("LIST_CACHE_REDIS_DATABASE_INDEX",
					CacheConfig.DEFAULT_LIST_CACHE_REDIS_DATABASE_INDEX));
			jedis.zremrangeByRank(cacheName, startIndex, endIndex);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 删除有序集缓存的一部分成员，按照socre值的范围
	 * @param cacheName
	 * @param startIndex
	 * @param endIndex
	 */
	public void zDelByScore(String cacheName, double minScore, double maxScore) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.select(config.getInt("LIST_CACHE_REDIS_DATABASE_INDEX",
					CacheConfig.DEFAULT_LIST_CACHE_REDIS_DATABASE_INDEX));
			jedis.zremrangeByScore(cacheName, minScore, maxScore);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
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
			String cacheType = jedis.type(cacheName);
			if (!Strings.isEmpty(cacheType) && cacheType.equals("zset")) {
				jedis.select(config.getInt("LIST_CACHE_REDIS_DATABASE_INDEX",
						CacheConfig.DEFAULT_LIST_CACHE_REDIS_DATABASE_INDEX));
			} else {
				jedis.select(config.getInt("STANDARD_CACHE_REDIS_DATABASE_INDEX",
						CacheConfig.DEFAULT_STANDARD_CACHE_REDIS_DATABASE_INDEX));
			}
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
