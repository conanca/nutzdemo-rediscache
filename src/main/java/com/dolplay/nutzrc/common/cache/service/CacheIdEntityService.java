package com.dolplay.nutzrc.common.cache.service;

import org.nutz.dao.Dao;
import org.nutz.service.IdEntityService;

import com.dolplay.nutzrc.common.cache.dao.CacheDao;

public class CacheIdEntityService<T> extends IdEntityService<T> implements CacheService {
	private CacheDao cacheDao;

	public CacheIdEntityService(Dao dao, CacheDao cacheDao) {
		super(dao);
		this.cacheDao = cacheDao;
	}

	public CacheDao cacheDao() {
		return cacheDao;
	}

}
