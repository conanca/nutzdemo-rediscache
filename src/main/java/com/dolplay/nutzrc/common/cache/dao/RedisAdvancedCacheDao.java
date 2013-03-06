package com.dolplay.nutzrc.common.cache.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nutz.ioc.impl.PropertiesProxy;

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
	 * @param cacheKey
	 * @param seconds
	 * @param score
	 * @param item
	 */
	public void zAdd(String cacheKey, int seconds, double score, String item) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			boolean isNew = !jedis.exists(cacheKey);
			jedis.zadd(cacheKey, score, item);
			if (isNew && seconds > 0) {
				jedis.expire(cacheKey, seconds);
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
	 * @param cacheKey
	 * @param score
	 * @param item
	 */
	public void zAdd(String cacheKey, double score, String item) {
		int timeout = config.getInt("LIST_CACHE_TIMEOUT", CacheConfig.DEFAULT_LIST_CACHE_TIMEOUT);
		zAdd(cacheKey, timeout, score, item);
	}

	/**
	 * 查询有序集缓存，按照区间及排序方式
	 * 如：startIndex=0 endIndex=9 order=Order.Desc，按倒序取第1条-第10条
	 * @param cacheKey
	 * @param key
	 * @param startIndex
	 * @param endIndex
	 * @param order
	 * @return
	 */
	public List<String> zQueryByRank(String cacheKey, long startIndex, long endIndex, Order order) {
		Jedis jedis = null;
		List<String> valueList = null;
		try {
			jedis = jedisPool.getResource();
			Set<String> valueSet = null;
			if (order.equals(Order.Asc)) {
				valueSet = jedis.zrange(cacheKey, startIndex, endIndex);
			} else {
				valueSet = jedis.zrevrange(cacheKey, startIndex, endIndex);
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
	 * @param cacheKey
	 * @param key
	 * @param minScore
	 * @param maxScore
	 * @param order
	 * @return
	 */
	public List<String> zQueryByScore(String cacheKey, double minScore, double maxScore, Order order) {
		Jedis jedis = null;
		List<String> valueList = null;
		try {
			jedis = jedisPool.getResource();
			Set<String> valueSet = null;
			if (order.equals(Order.Asc)) {
				valueSet = jedis.zrangeByScore(cacheKey, minScore, maxScore);
			} else {
				valueSet = jedis.zrevrangeByScore(cacheKey, minScore, maxScore);
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
	 * @param cacheKey
	 * @param items
	 */
	public void zDel(String cacheKey, String... items) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.zrem(cacheKey, items);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 删除有序集缓存的一部分成员，按照区间
	 * @param cacheKey
	 * @param startIndex
	 * @param endIndex
	 */
	public void zDelByRank(String cacheKey, long startIndex, long endIndex) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.zremrangeByRank(cacheKey, startIndex, endIndex);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 删除有序集缓存的一部分成员，按照socre值的范围
	 * @param cacheKey
	 * @param startIndex
	 * @param endIndex
	 */
	public void zDelByScore(String cacheKey, double minScore, double maxScore) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.zremrangeByScore(cacheKey, minScore, maxScore);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 为给定缓存设置生存时间，当缓存 过期时(生存时间为 0 )，它会被自动删除
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
}
