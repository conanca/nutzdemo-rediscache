package com.dolplay.nutzrc.common.cache.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nutz.ioc.impl.PropertiesProxy;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.dolplay.nutzrc.common.cache.Order;

public class RedisAdvancedCacheDao extends RedisCacheDao implements AdvancedCacheDao {

	public RedisAdvancedCacheDao(PropertiesProxy config, JedisPool jedisPool) {
		super(config, jedisPool);
	}

	/**
	 * 为有序集缓存的值增添一个成员，需指定该成员的score
	 * @param cacheName
	 * @param score
	 * @param item
	 */
	public void zAdd(String cacheName, double score, String item) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.zadd(cacheName, score, item);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
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
			jedis.zremrangeByScore(cacheName, minScore, maxScore);
		} catch (Exception e) {
			throw e;
		} finally {
			if (jedis != null)
				jedisPool.returnResource(jedis);
		}
	}
}
