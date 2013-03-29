nutzdemo-rediscache
===================
## 注意

本项目不再更新，已改造成nutz插件，请参考[nutz-cache](https://github.com/conanca/nutz-cache)

## Description

本项目演示了一个应用如何通过nutz.Aop实现使用redis作为数据库的缓存

* 提供了一个缓存预先读取及缓存自动设值的方法拦截器，用于拦截数据库查询的方法
* 提供了用于手动操作缓存的CacheDao
* 支持普通类型缓存和有序集型缓存
