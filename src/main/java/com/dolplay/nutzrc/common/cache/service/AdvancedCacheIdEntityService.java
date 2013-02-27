package com.dolplay.nutzrc.common.cache.service;

import org.nutz.dao.Dao;
import org.nutz.service.IdEntityService;

import com.dolplay.nutzrc.common.cache.dao.AdvancedCacheDao;

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
