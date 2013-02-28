package com.dolplay.nutzrc.common.cache;

import org.nutz.lang.Strings;

/**
 * 缓存相关的字符串操作的帮助类
 * @author conanca
 *
 */
public class CStrings {

	//	/**
	//	 * 根据给定的缓存名称前缀和后缀，拼接一个缓存的名称
	//	 * @param cacheNamePrefix
	//	 * @param cacheNameSuffixs
	//	 * @return
	//	 */
	//	public static String cacheName(String cacheNamePrefix, Object... cacheNameSuffixs) {
	//		String cacheName = null;
	//		StringBuilder sb = new StringBuilder(cacheNamePrefix);
	//		if (cacheNameSuffixs != null && cacheNameSuffixs.length > 0) {
	//			for (Object cacheNameSuffix : cacheNameSuffixs) {
	//				if (cacheNameSuffix != null) {
	//					sb.append(CacheConfig.CACHENAME_DELIMITER);
	//					sb.append(cacheNameSuffix);
	//				}
	//			}
	//		}
	//		return cacheName;
	//	}

	/**
	 * 根据给定的缓存名称前缀和后缀，拼接一个缓存的名称
	 * @param cacheNamePrefix
	 * @param cacheNameSuffixs
	 * @return
	 */
	public static String cacheName(String cacheNamePrefix, String... cacheNameSuffixs) {
		String cacheName = null;
		StringBuilder sb = new StringBuilder(cacheNamePrefix);
		if (cacheNameSuffixs != null && cacheNameSuffixs.length > 0) {
			for (String cacheNameSuffix : cacheNameSuffixs) {
				if (!Strings.isEmpty(cacheNameSuffix)) {
					sb.append(CacheConfig.CACHENAME_DELIMITER);
					sb.append(cacheNameSuffix);
				}
			}
		}
		return cacheName;
	}

	/**
	 * 根据给定的缓存名称前缀和后缀，拼接一个缓存的名称
	 * @param cacheNamePrefix
	 * @param cacheNameSuffixs
	 * @return
	 */
	public static String cacheName(String cacheNamePrefix, int... cacheNameSuffixs) {
		String cacheName = null;
		StringBuilder sb = new StringBuilder(cacheNamePrefix);
		if (cacheNameSuffixs != null && cacheNameSuffixs.length > 0) {
			for (int cacheNameSuffix : cacheNameSuffixs) {
				sb.append(CacheConfig.CACHENAME_DELIMITER);
				sb.append(cacheNameSuffix);
			}
		}
		return cacheName;
	}

	/**
	 * 根据给定的缓存名称前缀和后缀，拼接一个缓存的名称
	 * @param cacheNamePrefix
	 * @param cacheNameSuffixs
	 * @return
	 */
	public static String cacheName(String cacheNamePrefix, long... cacheNameSuffixs) {
		String cacheName = null;
		StringBuilder sb = new StringBuilder(cacheNamePrefix);
		if (cacheNameSuffixs != null && cacheNameSuffixs.length > 0) {
			for (long cacheNameSuffix : cacheNameSuffixs) {
				sb.append(CacheConfig.CACHENAME_DELIMITER);
				sb.append(cacheNameSuffix);
			}
		}
		return cacheName;
	}
}
