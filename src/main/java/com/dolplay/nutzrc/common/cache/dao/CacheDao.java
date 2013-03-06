package com.dolplay.nutzrc.common.cache.dao;

import java.util.Set;

/**
 * 缓存DAO，用于操作缓存
 * @author conanca
 *
 */
public interface CacheDao {
	/**
	 * 增加一个缓存，如果存在将更新该缓存。注：缓存超时时间由配置文件配置
	 * @param cacheKey
	 * @param cacheValue
	 */
	public void set(String cacheKey, Object cacheValue);

	/**
	 * 增加一个缓存，如果存在将更新该缓存。可指定缓存超时时间，如果超时时间小于等于0，则为永久缓存
	 * @param cacheKey
	 * @param timeout
	 * @param cacheValue
	 */
	public void set(String cacheKey, int timeout, Object cacheValue);

	/**
	 * 根据缓存名获取一个缓存的值
	 * @param cacheKey
	 * @return
	 */
	public String get(String cacheKey);

	/**
	 * 删除一个或多个缓存。
	 * @param cacheKeys
	 * @return
	 */
	public long remove(String... cacheKeys);

	/**
	 * 为给定缓存设置超时时间，当缓存 过期时(超时时间为 0 )，它会被自动删除
	 * @param cacheKey
	 * @param seconds
	 * @return
	 */
	public boolean expire(String cacheKey, int seconds);

	/**
	 * 判断给定 key 是否存在
	 * @param cacheKey
	 * @return
	 */
	public boolean exists(String cacheKey);

	/**
	 * 查找所有符合给定模式 pattern 的 key
	 * 
	 * KEYS * 匹配数据库中所有 key 。
	 * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
	 * KEYS h*llo 匹配 hllo 和 heeeeello 等。
	 * KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
	 * 特殊符号用 \ 隔
	 * 
	 * @param pattern
	 * @return
	 */
	public Set<String> keySet(String pattern);
}
