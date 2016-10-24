# songwie-framework
基于springboot的框架,做到互联网分布式服务化架构，模块化开发。


<p>
	<br />
</p>
<p>
	<img src="https://github.com/songwie/songwie-framework/blob/master/%E5%9F%BA%E7%A1%80%E6%A1%86%E6%9E%B6%E5%8C%85%E5%9B%BE.png" alt="架构图" /> 
</p>
<p>
	<br />

<p>
	<br />
songwie-boot ：框架生命周期，基于spring-boot的基础框架。<br />
songwie-mq &nbsp; : 消息队列模块。<br />
songwie-elasticsearch ： 搜索模块<br />
songwie-cach ： 缓存模块<br />
songwie-jdbc ： 数据库模块（支持jpa/mybatis）<br />
songwie-service ： 服务化模块（只做接口）<br />
songwie-rpc ： 服务化拆分模块（集成dubbo）<br />
songwie-dao ： 数据层模块，底层数据对接songwie-jdbc，songwie-cache，songwie-elasticsearch 等基础层<br />
songwie-config：配置模块（集成百度配置中心baidu-disconf，做到修改配置无须重启）<br />
songwie-log ： 日志模块，统一管理日志封装与拦截，做到日志统一分析与记录。<br />
songwie-util： 工具类模块<br />
songwie-concurrent ：并发处理模块（集成分布式锁（zookeeper，redis），线程池调度等）<br />
songwie-web ： web模块<br />
songwie-restfull： restfull模块如果需要集成外部接口，app等。<br />
songwie-job： 定时任务调度模块（定时调度，长度进程调度，定时监控）<br />
songwie-security ： 安全模块（集成加密算法，restfull接口加密，sdk等）<br />
songwie-minitor ：（集成dianping-cat 做业务监控）<br />
songwie-job 模块：<br />
</p>
<p>
	<br />
</p>

<p>
	<br />
</p>
<p>
	<img src="http://songwie.com/attached/image/20150215/20150215114720_395.png" alt="" /> 
</p>
<p>
	<br />
</p>
<p>
	<img src="http://songwie.com/attached/image/20150215/20150215114739_785.png" alt="" /> 
</p>
<p>
	<br />
</p>

songwie-log 模块：

从url 到service 到数据库 串联整个流程日志，通过requestId 1893267191333888 串联整个证明周期。
<p>
	<br />
songwie-boot ：框架生命周期，基于spring-boot的基础框架。2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.aspect.LoginInterceptor - 1893267191333888-URL.Method POST http://localhost:8081/login<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.aspect.LoginInterceptor - 1893267191333888-URL.Host POST 0:0:0:0:0:0:0:1<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Detail 1893267191333888-com.songwie.system.web.UserController.login<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Class com.songwie.system.web.UserController<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Method login<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Args 1893267191333888-[92027, 123456]<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Detail 1893267191333888-com.songwie.system.service.UserService.checkLogin<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Class com.songwie.system.service.UserService<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Method checkLogin<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Args 1893267191333888-[92027, 123456]<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Detail 1893267191333888-com.songwie.system.dao.impl.UserDao.getEmployeeByNum<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Class com.songwie.system.dao.impl.UserDao<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Method getEmployeeByNum<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Args 1893267191333888-[org.apache.ibatis.executor.statement.RoutingStatementHandler@3279eb09]<br />
2016-10-24 19:46:48 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Return Size 69<br />
2016-10-24|7|0|statement|SELECT 'x'|SELECT 'x'<br />
2016-10-24 19:46:49 INFO &nbsp;com.songwie.common.jdbc.page.PagePlugin - 1893267191333888-SQL.Method1893267191333888-<br />
2016-10-24|71|5|statement|/*requestId:1893267191333888,ip:192.168.49.2,threadId:http-nio-8081-exec-1*/select * from user_employee where num = '92027'<br />
2016-10-24 19:46:49 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Return Size 503<br />
2016-10-24 19:46:49 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Return Size 503<br />
2016-10-24 19:46:49 INFO &nbsp;com.songwie.common.log.minitor.InvokeAspect - 1893267191333888-MethodExcuteStash.Return Size 12<br />
2016-10-24 19:46:49 INFO &nbsp;com.songwie.common.aspect.LoginInterceptor - 1893267191333888-View titles/login<br />
songwie-job 模块：
</p>
<p>
	<br />
</p>
