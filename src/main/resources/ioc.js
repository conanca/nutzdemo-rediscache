var ioc = {

	// 系统参数
	config : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		args : [false],
		fields : {
			paths : [ "system.properties" ]
		}
	},
	
	// 配置了cacheDao示例
	cacheDao: {
		type : "com.dolplay.nutzrc.common.cache.dao.RedisCacheDao",
		args : [	 {refer : 'config'},{refer : 'jedisPool'}]
	},
	
	// 缓存预先读取的方法拦截器配置
	cacheInterceptor: {
		type : "com.dolplay.nutzrc.common.cache.interceptor.CacheInterceptor",
		fields : {
			cacheDao : {refer : 'cacheDao'}
		}
	},
	
	// 配置了advancedCacheDao示例
	advancedCacheDao: {
		type : "com.dolplay.nutzrc.common.cache.dao.RedisAdvancedCacheDao",
		args : [	 {refer : 'config'},{refer : 'jedisPool'}]
	},
	
	// 有序集合型缓存预先读取的方法拦截器配置
	advancedCacheInterceptor: {
		type : "com.dolplay.nutzrc.common.cache.interceptor.AdvancedCacheInterceptor",
		fields : {
			cacheDao : {refer : 'advancedCacheDao'}
		}
	}
};