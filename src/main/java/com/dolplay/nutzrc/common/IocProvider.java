package com.dolplay.nutzrc.common;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;

public class IocProvider {
	public static Ioc ioc() {
		// TODO 实际项目中获取ioc的方式可灵活决定
		Ioc ioc = null;
		try {
			ioc = new NutIoc(new ComboIocLoader("*org.nutz.ioc.loader.json.JsonLoader", "ioc.js", "dao.js", "jedis.js",
					"*org.nutz.ioc.loader.annotation.AnnotationIocLoader", "com.dolplay.nutzrc"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ioc;
	}
}
