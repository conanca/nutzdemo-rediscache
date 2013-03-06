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
	 */
	public void zAdd(String cacheKey, int seconds, double score, String item);

	/**
	 * 为有序集缓存的值增添一个成员，需指定该成员的score
	 * 如果缓存不存在则创建这个缓存，注：缓存超时时间由配置文件配置
	 * @param cacheKey
	 * @param score
	 * @param item
	 */
	public void zAdd(String cacheKey, double score, String item);

	/**
	 * 查询有序集缓存，按照区间及排序方式
	 * 如：startIndex=0 endIndex=9 order=Order.Desc，按倒序取第1条-第10条
	 * @param cacheKey
	 * @param startIndex
	 * @param endIndex
	 * @param order
	 * @return
	 */
	public List<String> zQueryByRank(String cacheKey, long startIndex, long endIndex, Order order);

	/**
	 * 查询有序集缓存，按照score值范围及排序方式
	 * minScore=1997 maxScore=2013 order=Order.Desc，按倒序取score值在1997-2013的
	 * @param cacheKey
	 * @param minScore
	 * @param maxScore
	 * @param order
	 * @return
	 */
	public List<String> zQueryByScore(String cacheKey, double minScore, double maxScore, Order order);

	/**
	 * 删除有序集缓存的一部分成员，按照成员的值
	 * @param cacheKey
	 * @param items
	 */
	public void zDel(String cacheKey, String... items);

	/**
	 * 删除有序集缓存的一部分成员，按照区间
	 * @param cacheKey
	 * @param startIndex
	 * @param endIndex
	 */
	public void zDelByRank(String cacheKey, long startIndex, long endIndex);

	/**
	 * 删除有序集缓存的一部分成员，按照socre值的范围
	 * @param cacheKey
	 * @param startIndex
	 * @param endIndex
	 */
	public void zDelByScore(String cacheKey, double minScore, double maxScore);
}
