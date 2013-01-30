package com.dolplay.nutzrc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdEntityService;

import com.dolplay.nutzrc.cache.Cache;
import com.dolplay.nutzrc.cache.CacheNameSuffix;
import com.dolplay.nutzrc.cache.CRUD;
import com.dolplay.nutzrc.domain.User;

@IocBean(fields = { "dao" })
public class UserService extends IdEntityService<User> {
	public UserService() {
		super();
	}

	public UserService(Dao dao) {
		super(dao);
	}

	public Map<Long, String> map() {
		Map<Long, String> map = new HashMap<Long, String>();
		List<User> users = query(null, null);
		for (User user : users) {
			map.put(user.getId(), user.getName());
		}
		return map;
	}

	@Aop("cacheInterceptor")
	@Cache(oper = CRUD.READ, cacheNamePrefix = "cache:system:allusers")
	public List<User> list() {
		return query(null, null);
	}

	@Aop("cacheInterceptor")
	@Cache(oper = CRUD.READ, cacheNamePrefix = "cache:system:user")
	public User view(@CacheNameSuffix int id) {
		return fetch(id);
	}

	@Aop("cacheInterceptor")
	@Cache(delCaches = { "cache:system:allusers" })
	public User insert(User user) {
		return dao().insert(user);
	}

	@Aop("cacheInterceptor")
	@Cache(oper = CRUD.UPDATE, cacheNamePrefix = "cache:system:user", delCaches = { "cache:system:allusers" })
	public User update(@CacheNameSuffix int id, User user) {
		dao().update(user);
		return user;
	}

	@Aop("cacheInterceptor")
	@Cache(oper = CRUD.DELETE, cacheNamePrefix = "cache:system:user")
	public void remove(@CacheNameSuffix int id) {
		delete(id);
	}
}
