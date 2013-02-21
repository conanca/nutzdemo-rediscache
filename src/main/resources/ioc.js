var ioc = {

	// 系统参数
	config : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		args : [false],
		fields : {
			paths : [ "system.properties" ]
		}
	}
};