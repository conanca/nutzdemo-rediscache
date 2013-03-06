nutzdemo-rediscache
===================

## Description

本项目演示了一个应用如何通过nutz.Aop实现使用redis作为数据库的缓存

* 提供了一个缓存预先读取及缓存自动设值的方法拦截器，用于拦截数据库查询的方法
* 提供了用于手动操作缓存的CacheDao
* 支持普通类型缓存和有序集型缓存

## Example

`````{.java}
/**
 * @author Conanca
 * 对"用户"的相关操作的Service类
 * (演示使用缓存方法拦截器及手动操作缓存的一个示例)
 */
@IocBean(args = { "refer:dao", "refer:advancedCacheDao" })
public class UserAdvancedService extends AdvancedCacheIdEntityService<User> {
	private static Logger logger = LoggerFactory.getLogger(UserAdvancedService.class);

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
	@Cache(cacheKeyPrefix = CacheKeyPrefix.SYSTEM_ALLUSERS)
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
	@Aop("advancedCacheInterceptor")
	@Cache(cacheKeyPrefix = CacheKeyPrefix.SYSTEM_USER)
	public User view(@CacheKeySuffix int id) {
		return fetch(id);
	}

	/**
	 * 查询指定分页的用户列表
	 * 其中形参pager会被转为json字符串作为缓存名后缀
	 * @param pager
	 * @return
	 */
	@Aop("advancedCacheInterceptor")
	@Cache(cacheKeyPrefix = CacheKeyPrefix.SYSTEM_ALLUSERS_INPAGE)
	public List<User> listInPage(@CacheKeySuffix Pager pager) {
		return query(null, pager);
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
		logger.debug("立即更新缓存:" + CStrings.cacheKey(CacheKeyPrefix.SYSTEM_USER, id));
		cacheDao().set(CStrings.cacheKey(CacheKeyPrefix.SYSTEM_USER, id), user);
	}

	/**
	 * 删除一个用户,并手动删除相应缓存cache:system:user:[id]
	 * @param id
	 */
	public void remove(int id) {
		delete(id);
		// 立即删除缓存
		cacheDao().remove(CStrings.cacheKey(CacheKeyPrefix.SYSTEM_USER, id));
	}

	/**
	 * 手动删除全部用户列表缓存
	 */
	public void delAllUsersCache() {
		cacheDao().remove(CacheKeyPrefix.SYSTEM_ALLUSERS);
	}

	/**
	 * 根据指定性别查询用户Id列表
	 * 缓存类型为有序集合
	 * @param gender
	 * @return
	 */
	@Aop("advancedCacheInterceptor")
	@Cache(cacheKeyPrefix = CacheKeyPrefix.SYSTEM_ALLUSERS_IDLIST, cacheType = CacheType.List)
	public List<String> listIdByGender(@CacheKeySuffix String gender) {
		List<User> userList = query(Cnd.where("gender", "=", gender), null);
		List<String> idList = new ArrayList<String>();
		for (User u : userList) {
			idList.add(String.valueOf(u.getId()));
		}
		return idList;
	}

	/**
	 * 查询2007-01-01之前出生的用户的id列表
	 * 该示例演示手动操作有序集合缓存
	 * 缓存类型为有序集合(item值即用户id，score值为用户出生日期，按该值顺序存储)
	 * @return
	 * @throws ParseException
	 */
	public List<String> oldUserListId() throws ParseException {
		List<String> idList = null;
		idList = cacheDao().zQueryByRank(CStrings.cacheKey(CacheKeyPrefix.SYSTEM_OLDUSERS_IDLIST, MARKDATE), 0, -1,
				Order.Asc);
		if (idList == null || idList.size() == 0) {
			List<User> oldUserList = query(
					Cnd.where("birthday", "<=", new SimpleDateFormat("yyyy-MM-dd").parse(MARKDATE)).asc("birthday"),
					null);
			idList = new ArrayList<String>();
			for (User u : oldUserList) {
				String uId = String.valueOf(u.getId());
				idList.add(uId);
				cacheDao().zAdd(CStrings.cacheKey(CacheKeyPrefix.SYSTEM_OLDUSERS_IDLIST, MARKDATE),
						u.getBirthday().getTime(), uId);
			}
		}
		return idList;
	}

	/**
	 * 插入一个新用户，并手动更新缓存：2007-01-01之前出生的用户的id列表
	 * @param user
	 */
	public void insertAndUpdateCache(User user) {
		dao().insert(user);
		cacheDao().zAdd(CStrings.cacheKey(CacheKeyPrefix.SYSTEM_OLDUSERS_IDLIST, MARKDATE), user.getBirthday().getTime(),
				String.valueOf(user.getId()));
	}

	/**
	 * 删除一个用户，并手动更新缓存：2007-01-01之前出生的用户的id列表
	 * @param user
	 */
	public void deleteAndUpdateCache(User user) {
		dao().delete(user);
		cacheDao().zDel(CStrings.cacheKey(CacheKeyPrefix.SYSTEM_OLDUSERS_IDLIST, MARKDATE), String.valueOf(user.getId()));
	}
}