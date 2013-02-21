package com.dolplay.nutzrc.service;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.Ioc;
import org.nutz.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.dolplay.nutzrc.domain.User;
import com.dolplay.nutzrc.util.IocProvider;

public class UserServiceTest {
	private static Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
	private UserService userService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Ioc ioc = IocProvider.ioc();
		// 初始化数据库
		logger.debug("初始化数据库...");
		Dao dao = ioc.get(Dao.class, "dao");
		dao.create(User.class, true);
		FileSqlManager fm = new FileSqlManager("init_system_h2.sql");
		List<Sql> sqlList = fm.createCombo(fm.keys());
		dao.execute(sqlList.toArray(new Sql[sqlList.size()]));

		// 清空redis
		logger.debug("清空redis...");
		JedisPool pool = ioc.get(JedisPool.class, "jedisPool");
		Jedis jedis = pool.getResource();
		jedis.flushDB();
		pool.returnResource(jedis);
	}

	@Test
	public void test() {
		Ioc ioc = IocProvider.ioc();
		// 获取UserService示例
		userService = ioc.get(UserService.class);

		logger.debug("第一次执行view方法(获取一个user)...");
		userService.view(2);
		logger.debug("第二次执行view方法(获取一个user)...");
		User user = userService.view(2);
		logger.info(Json.toJson(user));

		logger.debug("第一次执行list方法...");
		List<User> users = userService.list();
		logger.info("userlist个数：" + users.size());
		logger.debug("第二次执行list方法...");
		users = userService.list();
		logger.info("userlist个数：" + users.size());

		logger.debug("插入新的user...");
		user = new User();
		user.setName("张三");
		user.setDescription("just for test");
		userService.insert(user);
		logger.debug("第三次执行list方法...");
		users = userService.list();
		logger.info("userlist个数：" + users.size());
		logger.debug("第四次执行list方法...");
		users = userService.list();
		logger.info("userlist个数：" + users.size());

		logger.debug("获取一个user并更新字段...");
		user = userService.view(3);
		user.setDescription("testttttttttttttttttttttttt");
		userService.update(3, user);
		user = userService.view(3);
		logger.info("更新后查到的user：" + Json.toJson(user));

		logger.debug("删除一个user...");
		userService.remove(9);
		logger.debug("尝试查询这个user...");
		user = userService.view(9);
		logger.info("查到的user:" + Json.toJson(user));
	}
}
