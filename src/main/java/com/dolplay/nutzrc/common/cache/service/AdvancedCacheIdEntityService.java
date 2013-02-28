package com.dolplay.nutzrc.common.cache.service;

import org.nutz.dao.Dao;
import org.nutz.service.IdEntityService;

import com.dolplay.nutzrc.common.cache.dao.AdvancedCacheDao;

/**
 * 提供高级缓存操作的Service基类
 * @author conanca
 *
 * @param <T>
 */
public class AdvancedCacheIdEntityService<T> extends IdEntityService<T> implements CacheService {
	private AdvancedCacheDao cacheDao;

	public AdvancedCacheIdEntityService(Dao dao, AdvancedCacheDao cacheDao) {
		super(dao);
		this.cacheDao = cacheDao;
	}

	public AdvancedCacheDao cacheDao() {
		return cacheDao;
	}

}
