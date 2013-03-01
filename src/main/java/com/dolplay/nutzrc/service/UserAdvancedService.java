package com.dolplay.nutzrc.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;

import com.dolplay.nutzrc.common.cache.CStrings;
import com.dolplay.nutzrc.common.cache.CacheName;
import com.dolplay.nutzrc.common.cache.CacheType;
import com.dolplay.nutzrc.common.cache.Order;
import com.dolplay.nutzrc.common.cache.annotation.Cache;
import com.dolplay.nutzrc.common.cache.annotation.CacheNameSuffix;
import com.dolplay.nutzrc.common.cache.dao.AdvancedCacheDao;
import com.dolplay.nutzrc.common.cache.service.AdvancedCacheIdEntityService;
import com.dolplay.nutzrc.domain.User;

/**
 * @author Conanca
 * 用户查询id集合等操作的Service类
 * 演示使用有序集合型缓存的一个示例
 */
@IocBean(args = { "refer:dao", "refer:advancedCacheDao" })
public class UserAdvancedService extends AdvancedCacheIdEntityService<User> {

	private static final String MARKDATE = "2007-01-01";

	public UserAdvancedService(Dao dao, AdvancedCacheDao listCacheDao) {
		super(dao, listCacheDao);
	}

	/**
	 * 查询全部用户返回一个列表
	 * 指定了缓存名为cache:system:allusers(先取相应缓存的值,如果有值则直接返回该值不执行本方法,没有值则执行该方法并设置缓存)
	 * 注: 方法必须有返回值,该值对应上述缓存的value
	 * @return
	 */
	@Aop("advancedCacheInterceptor")
	@Cache(cacheNamePrefix = CacheName.SYSTEM_ALLUSERS)
	public List<User> list() {
		return query(null, null);
	}

	@Aop("advancedCacheInterceptor")
	@Cache(cacheNamePrefix = CacheName.SYSTEM_ALLUSERS_INPAGE)
	public List<User> listInPage(@CacheNameSuffix Pager pager) {
		return query(null, pager);
	}

	/**
	 * 查询指定id的用户
	 * 指定了缓存名的前缀为cache:system:user,后缀为用户id(先取相应缓存的值,如果有值则直接返回该值不执行本方法,没有值则执行该方法并设置缓存)
	 * 注: 方法必须有返回值,该值对应上述缓存的value
	 * @param id
	 * @return
	 */
	@Aop("advancedCacheInterceptor")
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
		cacheDao().set(CStrings.cacheName(CacheName.SYSTEM_USER, id), user);
	}

	/**
	 * 删除一个用户,并手动删除相应缓存cache:system:user:[id]
	 * @param id
	 */
	public void remove(int id) {
		delete(id);
		// 立即删除缓存
		cacheDao().remove(CStrings.cacheName(CacheName.SYSTEM_USER, id));
	}

	/**
	 * 手动删除全部用户列表缓存
	 */
	public void delCacheForTest() {
		cacheDao().remove(CacheName.SYSTEM_ALLUSERS);
	}

	@Aop("advancedCacheInterceptor")
	@Cache(cacheNamePrefix = CacheName.SYSTEM_ALLUSERS_IDLIST, cacheType = CacheType.List)
	public List<String> listIdByGender(@CacheNameSuffix String gender) {
		List<User> userList = query(Cnd.where("gender", "=", gender), null);
		List<String> idList = new ArrayList<String>();
		for (User u : userList) {
			idList.add(String.valueOf(u.getId()));
		}
		return idList;
	}

	public List<String> oldUserListId() throws ParseException {
		List<String> idList = null;
		idList = cacheDao().zQueryByRank(CStrings.cacheName(CacheName.SYSTEM_OLDUSERS_IDLIST, MARKDATE), 0, -1,
				Order.Asc);
		if (idList == null || idList.size() == 0) {
			List<User> oldUserList = query(
					Cnd.where("birthday", "<=", new SimpleDateFormat("yyyy-MM-dd").parse(MARKDATE)).asc("birthday"),
					null);
			idList = new ArrayList<String>();
			for (User u : oldUserList) {
				String uId = String.valueOf(u.getId());
				idList.add(uId);
				cacheDao().zAdd(CStrings.cacheName(CacheName.SYSTEM_OLDUSERS_IDLIST, MARKDATE),
						u.getBirthday().getTime(), uId);
			}
		}
		return idList;
	}

	public void insertAndUpdateCache(User user) {
		dao().insert(user);
		cacheDao().zAdd(CStrings.cacheName(CacheName.SYSTEM_OLDUSERS_IDLIST, MARKDATE), user.getBirthday().getTime(),
				String.valueOf(user.getId()));
	}

	public void deleteAndUpdateCache(User user) {
		dao().delete(user);
		cacheDao().zDel(CStrings.cacheName(CacheName.SYSTEM_OLDUSERS_IDLIST, MARKDATE), String.valueOf(user.getId()));
	}
}
