<h3>一、服务框架应用场景：</h3><br>
    本框架为基于linux的java框架，主要分为四个部分:<br>
    1.基于反射的业务框架，能够从数据库读取配置生成对应的服务并启动，而且能实时从数据库加载最新的配置。<br>
    2.基于nio的tcpClient和tcpServer,只需要指定ip、端口、接收数据的接口以及其他配置信息即可启动，能够断线自动重连。<br>
    3.基于反射的数据库操作模块，能够实现java对象到数据库表的映射。只要对象实现了DatabaseObject接口，均可以实现直接从数据库查询，更新，插入，删除等操作，全程无需任何sql拼写。<br>
    4.日志模块，文件模块，基于动态代理的数据库连接池，线程池，linux命令行参数获取模块等等。<br>
<h3>二、框架运行需求：</h3><br>
    1.需要相关的数据库用来存储服务配置。<br>
    2.数据库需要有tb_service_state表来实现对框架的控制。<br>
    3.单个服务的数据库表需要id,state,changed字段来实现对服务的控制。<br>
<h3>三、关于框架的详细介绍请看<a href="http://blog.csdn.net/deginch/article/details/52876254">博客</a>。</h3><br>
