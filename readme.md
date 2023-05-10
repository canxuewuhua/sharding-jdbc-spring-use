

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