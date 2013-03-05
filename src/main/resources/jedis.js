var ioc = {

	jedisPoolConfig : {
		type : 'redis.clients.jedis.JedisPoolConfig',
		fields : {
			maxActive : 1024,
			maxIdle : 200,
			maxWait : 1000,
			testOnBorrow : true,
			testOnReturn : true
		}
	},
	
	jedisPool : {
		type : 'redis.clients.jedis.JedisPool',
		events : {
			depose : 'destroy'
		},
		args : [ 
		    {
				refer : 'jedisPoolConfig'
			},
			'127.0.0.1',		// host
			6379,				// port
			2000,				// timeout
			null,				// password
			0						// database number
		]
	}
};