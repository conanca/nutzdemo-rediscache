package com.dolplay.nutzrc.common.cache.dao;

public interface CacheDao {
	/**
	 * 增加一个缓存，如果存在将更新该缓存。注：缓存超时时间由配置文件配置
	 * @param cacheName
	 * @param cacheValue
	 */
	public void set(String cacheName, Object cacheValue);

	/**
	 * 增加一个缓存，如果存在将更新该缓存。可指定缓存超时时间，如果超时时间小于等于0，则为永久缓存
	 * @param cacheName
	 * @param timeout
	 * @param cacheValue
	 */
	public void set(String cacheName, int timeout, Object cacheValue);

	/**
	 * 根据缓存名获取一个缓存的值
	 * @param cacheName
	 * @return
	 */
	public String get(String cacheName);

	/**
	 * 删除一个或多个缓存。
	 * @param cacheNames
	 * @return
	 */
	public long remove(String... cacheNames);
}