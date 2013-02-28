package com.dolplay.nutzrc.common.cache.dao;

import java.util.List;

import com.dolplay.nutzrc.common.cache.Order;

/**
 * 高级缓存DAO，可操作有序集缓存
 * @author conanca
 *
 */
public interface AdvancedCacheDao extends CacheDao {
	/**
	 * 为有序集缓存的值增添一个成员，需指定该成员的score
	 * @param cacheName
	 * @param score
	 * @param item
	 */
	public void zAdd(String cacheName, double score, String item);

	/**
	 * 查询有序集缓存，按照区间及排序方式
	 * 如：startIndex=0 endIndex=9 order=Order.Desc，按倒序取第1条-第10条
	 * @param cacheName
	 * @param startIndex
	 * @param endIndex
	 * @param order
	 * @return
	 */
	public List<String> zQueryByRank(String cacheName, long startIndex, long endIndex, Order order);

	/**
	 * 查询有序集缓存，按照score值范围及排序方式
	 * minScore=1997 maxScore=2013 order=Order.Desc，按倒序取score值在1997-2013的
	 * @param cacheName
	 * @param minScore
	 * @param maxScore
	 * @param order
	 * @return
	 */
	public List<String> zQueryByScore(String cacheName, double minScore, double maxScore, Order order);

	/**
	 * 删除有序集缓存的一部分成员，按照成员的值
	 * @param cacheName
	 * @param items
	 */
	public void zDel(String cacheName, String... items);

	/**
	 * 删除有序集缓存的一部分成员，按照区间
	 * @param cacheName
	 * @param startIndex
	 * @param endIndex
	 */
	public void zDelByRank(String cacheName, long startIndex, long endIndex);

	/**
	 * 删除有序集缓存的一部分成员，按照socre值的范围
	 * @param cacheName
	 * @param startIndex
	 * @param endIndex
	 */
	public void zDelByScore(String cacheName, double minScore, double maxScore);
}
