

ShardingJDBC 3.0后更新为 ShardingShpere

以jar方式与业务代码融合 不像MyCat和代码没有整合

ShardingShpere功能
数据分片 分库 & 分表
读写分离
分片策略定制化
无中心化分布式主键

ubuntu安装多个mysql的步骤可参考blog(关键点安装多个mysql服务 再然后就是配置主从同步-本机不同端口127.0.0.1)
https://blog.csdn.net/TaoismHuang/article/details/122947260
选择 Linux - Generic，下载mysql-5.7.31-linux-glibc2.12-x86_64.tar.gz

按照文章的步骤 在腾讯云服务上搭建 3307 3308 3309三个mysql服务
其中3307是平常需要连接数据库的场景使用 
3308 3309 两个mysql服务是用来学习mysql主从复制 读写分离 分库分表

搭建主从复制可参考狂神的B站视频+文章
https://www.kuangstudy.com/zl/sharding#1369645557613608962
由于只有一台服务器 可使用不同的端口号 3308 3309 来模拟不同的mysql服务器
1、 首先配置 master节点 my.conf   接着配置slave节点  my.cnf
2、 在master服务器授权slave服务器可以同步权限(master节点执行)
mysql > grant replication slave, replication client on *.* to 'root'@'slave服务的ip' identified by 'slave服务器的密码';
mysql > grant replication slave, replication client on *.* to 'root'@'127.0.0.1' identified by '123456';
注：由于是本机 ip不要写具体的ip地址 写为127.0.0.1
flush privileges;

3、  show master status; 查看master节点的日志文件名称和位置
4、  mysql > change master to master_host='127.0.0.1', master_user='root', master_password='654321', master_port=3308, master_log_file='mysql-bin.000003',master_log_pos=1084;
    注意： 此处的ip由于本机 也写为127.0.0.1

还有就是mysql服务器的启动和关闭命令：
 mysql服务的开启: sudo bin/mysqld_safe --user=mysql &
 mysql服务的关闭：  bin/mysqladmin -uroot -p654321 -S /mysql/mysql3308/mysqld.sock shutdown
 其中/mysql/mysql3308/mysqld.sock 是在my.cnf中配置的
还有一个命令是进入mysql
执行命令： bin/mysql -uroot -p -S /mysql/mysql3308/mysqld.sock

如果启动程序 控制台报错：MySql Host is blocked because of many connection errors; unblock with 'mysqladmin flush-hosts' 解决方法
数据库中进行，命令如下：
mysql > flush hosts;
如果程序报错：The last packet sent successfully to the server was 0 milliseconds ago
那就是yml中配置 useSSL=true
如果程序报错：A bean with that name has already been defined in class path resource（bean名字重复报错解决）
spring:
 main:
  allow-bean-definition-overriding: true #当遇到相同名字的时候，是否允许覆盖注册

如果查看 mysql > show master status;
MySQL配置主从同步失败：Slave_IO_Running：Connecting   说明主从同步并没有成功  自己遇到的原因就是：因为同台机器 ip要写为127.0.0.1


没有同步成功，则 需要 stop slave; reset slave;  start slave;
如果启动mysql时出现错误：Check that mysqld is running and that the socket: '/tmp/mysql.sock' exists!
ln -s /usr/local/mysql/mysql.sock /tmp/mysql.sock 


如果提示缺少安装包 libaio
apt-get install libaio1 libaio-dev

新建工程 并在yml文件中配置读写分离  
达到的效果就是： ds1主库负责写入  ds1,ds2主库和从库负责读取数据
本程序使用的远程服务器是 腾讯云 101.42.156.68


垂直拆分： 业务模块拆分、比如商品库，用户库，订单库
水平拆分： 对表进行水平拆分（比如说将数据拆分到不同的数据库的数据库表中）
表的垂直拆分：表的字段过多，字段的使用的频率不一 可以拆分两个表建立 1：1的关系

如何做到不停机分库分表 数据迁移呢？？
一般数据库的拆分也是一个过程的，一开始是单表 后面慢慢拆分成多表。

1、利用mysql+canal 做增量数据同步，利用分库分表中间件 将数据路由到对应的新表中

2、利用分库分表中间件，全量数据导入到对应的新表中
   怎么理解呢？比如对现有数据  insert select语句 进行全量数据导新表
   然后再进行服务上的重新部署 原有的数据先不进行清除 不允许新增数据，但允许查询 待数据同步完成后 再允许写入
   数据稳定后，将单表的配置切换到分库分表配置上

分库分表的方式： 水平拆分 垂直拆分
 水平拆分：就是将一个表的数据拆分到不同库 不同表中 可以根据时间、地区、或某个业务键纬度，也可以通过hash进行拆分
         最后通过路由访问到具体的数据。
 垂直拆分：就是把一个很多字段的表给拆分成多个表 每个表包含不同的冷热字段

逻辑表：水平拆分的数据库或数据表的相同路基和数据结构表的总称。
比如用户数据表根据用户id%2 拆分为2个表。 分别是：ksd_user0  和  ksd_user1。他们的逻辑表名是：ksd_user。

分库分表的分片策略怎么选择？？
比如说我们针对某个逻辑表
    对数据源的分片策略是什么，可以选择user_id作为分片键 分片算法是user_id%2 落到不同的数据源上
    对表的分片策略是什么，我们可以再选择一个字段oder_id作为分片键，分片算法是 oder_id%5 落到不同的物理表上
  
    所以总结来说就是怎么选择数据库 怎么选择数据表进行数据的存储
    一个是选择分片键 一个是拿这个分片键使用什么算法将数据落在表中

    分布式主键配置使用雪花算法
    ShardingShpere 主键列不能自增长  数据类型是 bigint(20)

Sharding-JDBC的核心/工原理

Sharding-JDBC数据分片主要流程是由SQL解析 →执行器优化 → SQL路由 →SQL改写 →SQL执行 →结果归并的流程组成。

从一个最简单的例子开始，若逻辑SQL为：
SELECT order_id FROM t_order WHERE order_id=1;
假设该SQL配置分片键order_id，并且order_id=1的情况，将路由至分片表1。那么改写之后的SQL应该为：
SELECT order_id FROM t_order_1 WHERE order_id=1;

再比如insert
insert into ksd_order(userid, orderid) values(1,2)
ds$->{0,1}.ksd_user$->{0..1}
userid%2   orderid%2
ds1           ksd_order0
最后改写sql
insert into ds1.ksd_order0(userid,orderid) values(1,2)

验证表关联 非常重要====
select * from ksd_user u left join ksd_order o on u.orderid = o.id
---------
: Actual SQL: ds0 ::: select * from ksd_user0 u left join ksd_order0 o on u.orderid = o.id
: Actual SQL: ds0 ::: select * from ksd_user0 u left join ksd_order1 o on u.orderid = o.id
: Actual SQL: ds0 ::: select * from ksd_user1 u left join ksd_order0 o on u.orderid = o.id
: Actual SQL: ds0 ::: select * from ksd_user1 u left join ksd_order1 o on u.orderid = o.id
: Actual SQL: ds1 ::: select * from ksd_user0 u left join ksd_order0 o on u.orderid = o.id
: Actual SQL: ds1 ::: select * from ksd_user0 u left join ksd_order1 o on u.orderid = o.id
: Actual SQL: ds1 ::: select * from ksd_user1 u left join ksd_order0 o on u.orderid = o.id
: Actual SQL: ds1 ::: select * from ksd_user1 u left join ksd_order1 o on u.orderid = o.id
总结：其实尽管是关联查询 也是根据分片键 穷举所有  笛卡尔积 列出所有可能的结果 最后将结果集归并


Sharding-JDBC数据分片主要流程是由SQL解析 →执行器优化 → SQL路由 →SQL改写 →SQL执行 →结果归并的流程组成。

