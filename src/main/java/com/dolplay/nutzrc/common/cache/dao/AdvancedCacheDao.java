package com.dolplay.nutzrc.common.cache.dao;

import java.util.List;

import com.dolplay.nutzrc.common.cache.Order;

/**
 * 高级缓存DAO，可操作有序集缓存(注意：有序集缓存都是永久缓存)
 * @author conanca
 *
 */
public interface AdvancedCacheDao extends CacheDao {

	/**
	 * 为有序集缓存的值增添一个成员，需指定该成员的score。
	 * 如果缓存不存在则创建这个缓存，并指定缓存超时时间；如果超时时间小于等于0，则为永久缓存
	 * @param cacheKey
	 * @param seconds
	 * @param score
	 * @param item
	 * @throws Exception
	 */
	public void zAdd(String cacheKey, int seconds, double score, String item) throws Exception;

	/**
	 * 为有序集缓存的值增添一个成员，需指定该成员的score
	 * 如果缓存不存在则创建这个缓存，注：缓存超时时间由配置文件配置
	 * @param cacheKey
	 * @param score
	 * @param item
	 * @throws Exception
	 */
	public void zAdd(String cacheKey, double score, String item) throws Exception;

	/**
	 * 查询有序集缓存，按照区间及排序方式
	 * 如：startIndex=0 endIndex=9 order=Order.Desc，按倒序取第1条-第10条
	 *        （查全部的：startIndex=0 endIndex=-1）
	 * @param cacheKey
	 * @param startIndex
	 * @param endIndex
	 * @param order
	 * @return
	 * @throws Exception
	 */
	public List<String> zQueryByRank(String cacheKey, long startIndex, long endIndex, Order order) throws Exception;

	/**
	 * 查询有序集缓存，按照区间
	 * 如：startIndex=0 endIndex=9 order=Order.Desc，按倒序取第1条-第10条
	 *        （查全部的：startIndex=0 endIndex=-1）
	 * @param cacheKey
	 * @param startIndex
	 * @param endIndex
	 * @return
	 * @throws Exception
	 */
	public List<String> zQueryByRank(String cacheKey, long startIndex, long endIndex) throws Exception;

	/**
	 * 查询有序集缓存，按照score值范围及排序方式
	 * minScore=1997 maxScore=2013 order=Order.Desc，按倒序取score值在1997-2013的
	 * （查全部的：minScore=0 maxScore=-1）
	 * @param cacheKey
	 * @param minScore
	 * @param maxScore
	 * @param order
	 * @return
	 * @throws Exception
	 */
	public List<String> zQueryByScore(String cacheKey, double minScore, double maxScore, Order order) throws Exception;

	/**
	 * 查询有序集缓存，按照score值范围
	 * minScore=1997 maxScore=2013 order=Order.Desc，按倒序取score值在1997-2013的
	 * （查全部的：minScore=0 maxScore=-1）
	 * @param cacheKey
	 * @param minScore
	 * @param maxScore
	 * @return
	 * @throws Exception
	 */
	public List<String> zQueryByScore(String cacheKey, double minScore, double maxScore) throws Exception;

	/**
	 * 查询有序集缓存（全部item），按照排序方式
	 * @param cacheKey
	 * @param order
	 * @return
	 * @throws Exception
	 */
	public List<String> zQueryAll(String cacheKey, Order order) throws Exception;

	/**
	 * 查询有序集缓存（全部item）
	 * @param cacheKey
	 * @return
	 * @throws Exception
	 */
	public List<String> zQueryAll(String cacheKey) throws Exception;

	/**
	 * 删除有序集缓存的一部分成员，按照成员的值
	 * @param cacheKey
	 * @param items
	 * @throws Exception
	 */
	public void zDel(String cacheKey, String... items) throws Exception;

	/**
	 * 删除有序集缓存的一部分成员，按照区间
	 * @param cacheKey
	 * @param startIndex
	 * @param endIndex
	 * @throws Exception
	 */
	public void zDelByRank(String cacheKey, long startIndex, long endIndex) throws Exception;

	/**
	 * 删除有序集缓存的一部分成员，按照socre值的范围
	 * @param cacheKey
	 * @param startIndex
	 * @param endIndex
	 * @throws Exception
	 */
	public void zDelByScore(String cacheKey, double minScore, double maxScore) throws Exception;
}
