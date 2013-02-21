package com.dolplay.nutzrc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdEntityService;

import com.dolplay.nutzrc.cache.Cache;
import com.dolplay.nutzrc.cache.CacheHelper;
import com.dolplay.nutzrc.cache.CacheNameSuffix;
import com.dolplay.nutzrc.domain.User;

/**
 * @author Conanca
 * 用户增删改查等操作的Service类
 * 演示使用redis缓存的一个示例
 */
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
	 * 指定了缓存名为cache:system:allusers(先取相应缓存的值,如果有值则直接返回该值不执行本方法,没有值则执行该方法并设置缓存)
	 * 注: 方法必须有返回值,该值对应上述缓存的value
	 * @return
	 */
	@Aop("cacheInterceptor")
	@Cache(cacheNamePrefix = CacheName.SYSTEM_ALLUSERS)
	public List<User> list() {
		return query(null, null);
	}

	/**
	 * 查询指定id的用户
	 * 指定了缓存名的前缀为cache:system:user,后缀为用户id(先取相应缓存的值,如果有值则直接返回该值不执行本方法,没有值则执行该方法并设置缓存)
	 * 注: 方法必须有返回值,该值对应上述缓存的value
	 * @param id
	 * @return
	 */
	@Aop("cacheInterceptor")
	@Cache(cacheNamePrefix = CacheName.SYSTEM_USER)
	public User view(@CacheNameSuffix int id) {
		return fetch(id);
	}

	/**
	 * 插入一个用户
	 * @param user
	 * @return
	 */
	public void insert(User user) {
		dao().insert(user);
	}

	/**
	 * 更新一个用户,并手动更新相应缓存cache:system:user:[id]
	 * @param id
	 * @param user
	 * @return
	 */
	public void update(int id, User user) {
		dao().update(user);
		// 立即更新缓存
		CacheHelper.set(CacheName.SYSTEM_USER + ":" + id, user, false);
	}

	/**
	 * 删除一个用户,并手动删除相应缓存cache:system:user:[id]
	 * @param id
	 */
	public void remove(int id) {
		delete(id);
		// 立即删除缓存
		CacheHelper.remove(CacheName.SYSTEM_USER + ":" + id);
	}

	/**
	 * 手动删除一个缓存
	 */
	public void delCacheForTest() {
		CacheHelper.remove(CacheName.SYSTEM_USER + ":1");
	}
}
