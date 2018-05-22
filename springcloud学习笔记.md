# SpringCloud简介 #
spring cloud 为开发人员提供了快速构建分布式系统的一些工具，包括配置管理、服务发现、断路器、路由、微代理、事件总线、全局锁、决策竞选、分布式会话等等。

在微服务架构中，需要几个基础的服务治理组件，包括服务注册与发现、服务消费、负载均衡、断路器、智能路由、配置管理等，由这几个基础组件相互协作，共同组建了一个简单的微服务系统。一个简答的微服务系统如下图：
![](http://upload-images.jianshu.io/upload_images/2279594-6b7c148110ebc56e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)

Spring Cloud版本演进情况如下：

    版本名称	      	 版本
    Finchley		snapshot版
    Edgware	    	snapshot版
    Dalston.SR4		当前最新稳定版本
    Camden.SR7		稳定版本
    Brixton.SR7		稳定版本
    Angel.SR6		稳定版本

Spring Cloud与Spring Boot版本匹配关系

    Spring Cloud					Spring Boot
    Finchley			兼容Spring Boot 2.0.x，不兼容Spring Boot 1.5.x
    Dalston和Edgware		兼容Spring Boot 1.5.x，不兼容Spring Boot 2.0.x
    Camden				兼容Spring Boot 1.4.x，也兼容Spring Boot 1.5.x
    Brixton				兼容Spring Boot 1.3.x，也兼容Spring Boot 1.4.x
    Angel				兼容Spring Boot 1.2.x

## 1、注册中心:Eureka-Server ##
在这里，我们需要用的的组件上Spring Cloud Netflix的Eureka ,eureka是一个服务注册和发现模块。
Eureka是一个高可用的组件，它没有后端缓存，每一个实例注册之后需要向注册中心发送心跳（因此可以在内存中完成），在默认情况下Erureka server也是一个eureka client ,必须要指定一个server。

**当出现如下这段话：**
*EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE.*
**说明Eureka进入自我保护状态，真实服务已经DOWN掉，但是注册中心界面仍然存在服务，且显示UP状态**

**产生原因:** 
Eureka Server在运行期间，会统计心跳失败的比例在15分钟之内是否
低于85%，如果出现低于的情况（在单机调试的时候很容易满足，实际在
生产环境上通常是由于网
络不稳定导致），Eureka Server会将当前的实例注册信息保护起来，同时提
示这个警告。保护模式主要用于一组客户端和Eureka Server之间存在网络分
区场景下的保护。一旦进入保护模式，Eureka Server将会尝试保护其服务注
册表中的信息，不再删除服务注册表中的数据（也就是不会注销任何微服务）。
**单机解决办法**

server端：

    1.关闭注册中心自我保护机制
    eureka.server.enable-self-preservation：false
    
    2.注册中心清理间隔（单位毫秒，默认60*1000）
    eureka.server.eviction-interval-timer-in-ms：10000

client端：

    1.开启健康检查（需要spring-boot-starter-actuator依赖）
    eureka.client.healthcheck.enabled:true
    2.租期更新时间间隔（默认30秒）
    eureka.instance.lease-renewal-interval-in-seconds=10
    3.租期到期时间（默认90秒）
    eureka.instance.lease-expiration-duration-in-seconds=15
以上参数配置下来，从服务停止，到注册中心清除不健康实例，时间大约在30秒左右。租期到期时间为30秒时，清除时间大约在59秒，若采用默认的30-60配置，清除时间大约在2分半（以上均在关闭保护机制情况下），生产环境建议采用默认配置，服务停止到注册中心清除实例之间有一些计算什么的。

## 2、服务提供者:Eureka-client ##
当client向server注册时，它会提供一些元数据，例如主机和端口，URL，主页等。Eureka server 从每个client实例接收心跳消息。 如果心跳超时，则通常将该实例从注册server中删除。
创建过程同server类似

## 3、服务消费者：RPC调用
在微服务架构中，业务都会被拆分成一个独立的服务，服务与服务的通讯是基于http restful的。Spring cloud有两种服务调用方式，一种是ribbon+restTemplate，另一种是feign。

## 3.1、负载均衡：Ribbon+RestTemplate ##
Ribbon是一个负载均衡客户端，可以很好的控制htt和tcp的一些行为。Feign默认集成了ribbon。单独使用Ribbon需要配合RestTemplate使用，在Ribbon中它会根据服务名来选择具体的服务实例，根据服务实例在请求的时候会用具体的url替换掉服务名
![](http://upload-images.jianshu.io/upload_images/2279594-9f10b702188a129d.png)

## 3.2、伪Http客户端：Feign ##
Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单。使用Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，可使用Feign 注解和JAX-RS注解。Feign支持可插拔的编码器和解码器。Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果。

简而言之：

Feign 采用的是基于接口的注解
Feign 整合了ribbon

## 4、断路器 Hystrix ##
在微服务架构中，根据业务来拆分成一个个的服务，服务与服务之间可以相互调用（RPC），在Spring Cloud可以用RestTemplate+Ribbon和Feign来调用。为了保证其高可用，单个服务通常会集群部署。由于网络原因或者自身的原因，服务并不能保证100%可用，如果单个服务出现问题，调用这个服务就会出现线程阻塞，此时若有大量的请求涌入，Servlet容器的线程资源会被消耗完毕，导致服务瘫痪。服务与服务之间的依赖性，故障会传播，会对整个微服务系统造成灾难性的严重后果，这就是服务故障的“雪崩”效应。

在微服务架构中，一个请求需要调用多个服务是非常常见的，如下图：

![](http://upload-images.jianshu.io/upload_images/2279594-08d8d524c312c27d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)

较底层的服务如果出现故障，会导致连锁故障。当对特定的服务的调用的不可用达到一个阀值（Hystric 是5秒20次） 断路器将会被打开。

![](http://upload-images.jianshu.io/upload_images/2279594-8dcb1f208d62046f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)

断路打开后，可用避免连锁故障，fallback方法可以直接返回一个固定值。

ribbo+template需要引入hystrix包，feign自带hystrix，但是需要在配置文件指定开启：feign.hystrix.enabled=true。

**需要特别注意的是：**

**1、之前的feign服务由于内置断路器支持，所以没有加@enablecircuitbreaker注解，但使用dashboard仪表盘时，必须要加，如果不加，dashboard无法接收到来自feign内部断路器的监控数据，会报“unable to connect to command metric stream”错误。**

**2、第一次访问被断路掉，原因第一次访问通常比较慢，Hystrix默认超时时间是1秒，解决办法：延长超时时间hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=5000。**

## 5、路由网关 Zuul ##
在Spring Cloud微服务系统中，一种常见的负载均衡方式是，客户端的请求首先经过负载均衡（zuul、Ngnix），再到达服务网关（zuul集群），然后再到具体的服务。

Zuul的主要功能是路由**转发**和**过滤器**。路由功能是微服务的一部分，比如／api/user转发到到user服务，/api/shop转发到到shop服务。zuul默认和Ribbon结合实现了负载均衡的功能。

**有一个问题：服务不可用时，断路器短时间会出现不生效情况，需要等服务被注册中心清理掉才生效。**

服务过滤：安全验证
继承ZuulFilter,重写run方法，实现过滤逻辑

## 6、分布式配置中心 Config ##
在分布式系统中，spring cloud config 提供一个服务端和客户端去提供可扩展的配置服务。我们可用用配置服务中心区集中的管理所有的服务的各种环境配置文件。配置服务中心采用**git的方式**存储配置文件，因此我们很容易部署修改，有助于对环境配置进行版本管理。

http请求地址和资源文件映射如下:

- /{application}/{profile}[/{label}]
- /{application}-{profile}.yml
- /{label}/{application}-{profile}.yml
- /{application}-{profile}.properties
- /{label}/{application}-{profile}.properties

![](https://upload-images.jianshu.io/upload_images/2279594-40ecbed6d38573d9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/417)

**遇到的坑：即使客户端spring.cloud.config.uri=XXXX其他端口，启动仍然连接8888端口**

查资料发现：SpringCloud里面有个“启动上下文”，主要是用于加载远端的配置，
默认加载顺序为：加载bootstrap.*里面的配置 --> 加载远程配置 --> 加载application.*里面的配置；

**SpringBoot启动完成后关闭的坑:注解RestController时，不能使用spring-boot-starter,而是spring-boot-starter-web**
 
> **Config集群化**

![](http://upload-images.jianshu.io/upload_images/2279594-babe706075d72c58.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)


## 7、消息总线 Bus ##
Spring Cloud Bus 将分布式的节点用轻量的消息代理连接起来。它可以用于广播配置文件的更改或者服务之间的通讯，也可以用于监控。

只需要发送post请求：http://localhost:port/bus/refresh，你会发现config-client会重新读取配置文件。

/bus/refresh接口可以指定服务，即使用”destination”参数，比如 “/bus/refresh?destination=customers:**” 即刷新服务名为customers的所有服务，不管ip。


![](http://upload-images.jianshu.io/upload_images/2279594-9a119d83cf90069f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

当git文件更改的时候，通过pc端用post向端口为8882的config-client发送请求/bus/refresh/；此时8882端口会发送一个消息，由消息总线向其他服务传递，从而使整个微服务集群都达到更新配置文件。

Tracing Bus Events： 
需要设置：spring.cloud.bus.trace.enabled=true，如果那样做的话，那么Spring Boot TraceRepository（如果存在）将显示每个服务实例发送的所有事件和所有的ack,比如：

    {
      "timestamp": "2015-11-26T10:24:44.411+0000",
      "info": {
      	"signal": "spring.cloud.bus.ack",
      	"type": "RefreshRemoteApplicationEvent",
      	"id": "c4d374b7-58ea-4928-a312-31984def293b",
      	"origin": "stores:8081",
      	"destination": "*:**"
    	}
      },
      {
      "timestamp": "2015-11-26T10:24:41.864+0000",
      "info": {
	    "signal": "spring.cloud.bus.sent",
	    "type": "RefreshRemoteApplicationEvent",
	    "id": "c4d374b7-58ea-4928-a312-31984def293b",
	    "origin": "customers:9000",
	    "destination": "*:**"
      	}
      },
      {
      "timestamp": "2015-11-26T10:24:41.862+0000",
      "info": {
	    "signal": "spring.cloud.bus.ack",
	    "type": "RefreshRemoteApplicationEvent",
	    "id": "c4d374b7-58ea-4928-a312-31984def293b",
	    "origin": "customers:9000",
	    "destination": "*:**"
     	 }
	}



## 8、服务链路跟踪 Sleuth-zipkin ##

Spring Cloud Sleuth 主要功能就是在分布式系统中提供追踪解决方案，并且兼容支持了 zipkin，你只需要在pom文件中引入相应的依赖即可。
微服务架构上通过业务来划分服务的，通过REST调用，对外暴露的一个接口，可能需要很多个服务协同才能完成这个接口功能，如果链路上任何一个服务出现问题或者网络超时，都会形成导致接口调用失败。随着业务的不断扩张，服务之间互相调用会越来越复杂。

![](http://upload-images.jianshu.io/upload_images/2279594-dd72907e82f89fd6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)

术语

- Span：基本工作单元，例如，在一个新建的span中发送一个RPC等同于发送一个回应请求给RPC，span通过一个64位ID唯一标识，trace以另一个64位ID表示，span还有其他数据信息，比如摘要、时间戳事件、关键值注释(tags)、span的ID、以及进度ID(通常是IP地址) 
span在不断的启动和停止，同时记录了时间信息，当你创建了一个span，你必须在未来的某个时刻停止它。
- Trace：一系列spans组成的一个树状结构，例如，如果你正在跑一个分布式大数据工程，你可能需要创建一个trace。
- Annotation：用来及时记录一个事件的存在，一些核心annotations用来定义一个请求的开始和结束 

	- cs - Client Sent -客户端发起一个请求，这个annotion描述了这个span的开始
	- sr - Server Received -服务端获得请求并准备开始处理它，如果将其sr减去cs时间戳便可得到网络延迟
	- ss - Server Sent -注解表明请求处理的完成(当请求返回客户端)，如果ss减去sr时间戳便可得到服务端需要的处理请求时间
	- cr - Client Received -表明span的结束，客户端成功接收到服务端的回复，如果cr减去cs时间戳便可得到客户端从服务端获	取回复的所有所需时间 

将Span和Trace在一个系统中使用Zipkin注解的过程图形化：

![](http://upload-images.jianshu.io/upload_images/2279594-4b865f2a2c271def.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/800)

**遇到的坑
1、需要注入AlwaysSampler
2、注意设置采样100%即：spring.sleuth.sampler.percentage=1
3、/info映射已经被actuator占用，无法使用。**

## 8、高可用注册中心集群 Eureka Server ##
Eureka通过运行多个实例，使其更具有高可用性。事实上，这是它默认的熟性，你需要做的就是给对等的实例一个合法的关联serviceurl。
解决方法：创建application-peer.properties。
**peer间相互注册。
Eureka-eserver peer1,Eureka-eserver peer2相互感应，当有服务注册时，两个Eureka-eserver是对等的，它们都存有相同的信息，这就是通过服务器的冗余来增加可靠性，当有一台服务器宕机了，服务并不会终止，因为另一台服务存有相同的数据。
