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

songwie-boot ：框架生命周期，基于spring-boot的基础框架。

songwie-mq   : 消息队列模块。

songwie-elasticsearch ： 搜索模块

songwie-cach ： 缓存模块

songwie-jdbc ： 数据库模块（支持jpa/mybatis）

songwie-service ： 服务化模块（只做接口）

songwie-rpc ： 服务化拆分模块（集成dubbo）

songwie-dao ： 数据层模块，底层数据对接songwie-jdbc，songwie-cache，songwie-elasticsearch 等基础层

songwie-config：配置模块（集成百度配置中心baidu-disconf，做到修改配置无须重启）

songwie-log ： 日志模块，统一管理日志封装与拦截，做到日志统一分析与记录。

songwie-util： 工具类模块

songwie-concurrent ：并发处理模块（集成分布式锁（zookeeper，redis），线程池调度等）

songwie-web ： web模块

songwie-restfull： restfull模块如果需要集成外部接口，app等。

songwie-job： 定时任务调度模块（定时调度，长度进程调度，定时监控）

songwie-security ： 安全模块（集成加密算法，restfull接口加密，sdk等）

songwie-minitor ：（集成dianping-cat 做业务监控）

songwie-job 模块：

<p>
	<br />
</p>
<p>
	<img src="http:songwie.com/attached/image/20150215/20150215114720_395.png" alt="" /> 
</p>
<p>
	<br />
</p>
<p>
	<img src="http:songwie.com/attached/image/20150215/20150215114739_785.png" alt="" /> 
</p>
<p>
	<br />
</p>

songwie-log 模块：

从url 到service 到数据库 串联整个流程日志。

