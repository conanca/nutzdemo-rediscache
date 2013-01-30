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

	/**
	 * 查询全部用户返回一个列表
	 * 指定了操作类型为READ,缓存名为cache:system:allusers(先取相应缓存的值,如果有值则直接返回该值不执行本方法,没有值则执行该方法并设置缓存)
	 * 注: 方法必须有返回值,该值对应上述缓存的value
	 * @return
	 */
	@Aop("cacheInterceptor")
	@Cache(oper = CRUD.READ, cacheNamePrefix = "cache:system:allusers")
	public List<User> list() {
		return query(null, null);
	}

	/**
	 * 查询指定id的用户
	 * 指定了操作类型为READ,缓存名的前缀为cache:system:user,后缀为用户id(先取相应缓存的值,如果有值则直接返回该值不执行本方法,没有值则执行该方法并设置缓存)
	 * 注: 方法必须有返回值,该值对应上述缓存的value
	 * @param id
	 * @return
	 */
	@Aop("cacheInterceptor")
	@Cache(oper = CRUD.READ, cacheNamePrefix = "cache:system:user")
	public User view(@CacheNameSuffix int id) {
		return fetch(id);
	}

	/**
	 * 插入一个用户
	 * 清除名为cache:system:allusers的缓存
	 * @param user
	 * @return
	 */
	@Aop("cacheInterceptor")
	@Cache(delCaches = { "cache:system:allusers" })
	public User insert(User user) {
		return dao().insert(user);
	}

	/**
	 * 更新一个用户
	 * 指定了操作类型为UPDATE,缓存名的前缀为cache:system:user,后缀为用户id(执行该方法并更新相应缓存),并且清除名为cache:system:allusers的缓存
	 * 注: 方法必须有返回值,该值对应上述缓存的value
	 * @param id
	 * @param user
	 * @return
	 */
	@Aop("cacheInterceptor")
	@Cache(oper = CRUD.UPDATE, cacheNamePrefix = "cache:system:user", delCaches = { "cache:system:allusers" })
	public User update(@CacheNameSuffix int id, User user) {
		dao().update(user);
		return user;
	}

	/**
	 * 删除一个用户
	 * 指定了操作类型为DELETE,缓存名的前缀为cache:system:user,后缀为用户id(执行该方法并清除相应缓存)
	 * @param id
	 */
	@Aop("cacheInterceptor")
	@Cache(oper = CRUD.DELETE, cacheNamePrefix = "cache:system:user")
	public void remove(@CacheNameSuffix int id) {
		delete(id);
	}
}
