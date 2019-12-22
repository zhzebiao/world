# Redis学习笔记

## 一、什么是Redis

1. 目前缓存的主流技术

   目前广泛使用的缓存技术有Redis和Memcached两种。从缓存命中的方面来讲，Memcached的性能更高，但是Redis提供的功能更加强大。两者的主要区别在与：Memcached支持多线程缓存，Redis只支持单线程缓存。

   

2. Redis的特性
   1. 多钟数据类型存储
      - 字符串类型
      - 散列类型
      - 列表类型
      - 集合类型、有序集合类型
   2. 内存存储与持久化
      - 内存的读写速度远快于硬盘
      - 自身提供了持久化功能（RDB、AOF两种方式）
   3. 功能丰富
      - 可用作缓存、队列、消息订阅/发布
      - 支持键的生存时间
      - 支持正则匹配删除键
   4. 简单稳定
      - 相比于SQL而言更加简单
      - 支持不同语言的客户端

3. Redis的多数据库

   Redis默认支持16个数据库，对外都是以一个从0开始的递增数字命名，可以通过修改配置文件databases参数增加/减少数据库个数。客户端连接Redis服务后会自动选择0号数据库，可以通过select命令更换数据库。

   - 不支持自动以数据库名称
   - 不支持为每个数据库设置访问密码
   - 多个数据库之间不完全隔离，FLUSHALL会清空所有数据库的数据

   ```shell
   select 1
   ```

   

## 二、Redis的数据类型

1. 字符串类型（String）

   ```shell
   # Redis管理字符串的基本命令
   set key value		# 设置指定键的值
   get key				# 获取指定键的值
   getrange key start end 		# 获取指定键的值的子字符串
   getset key value	# 返回指定键的值并设置新值
   getbit key offset	# 获取指定键的值中偏移处的bit值
   mget key1 [key2...] # 获取所有给定键的值
   
   setbit key offset value		# 设置指定键的值中偏移处的bit值
   setex key seconds value 	# 使用键和到期时间来设置值
   setnx key value		# 仅当键不存在的时候，设置键值
   setrange key offset value 	# 从指定偏移处开始用新值覆盖
   setlen key			# 获取指定键值的长度
   mset key value [key value...]		#为多个键设置值
   msetnx key value [key value...]		# 仅当键不存在时，为多个键设置值
   psetex key milliseconds value		# 设置键的值和到期时间
   incr key 			# 将键的整数值加1
   incrby key increament		# 将键的整数值增加给定值
   decr key			# 将键的整数值减少1
   decrby key decrement		# 将键的整数值减少给定值
   append key value	# 将指定值附加到键
   ```

   

2. 列表类型（List）

   ```shell
   # Redis管理列表的基本命令
   lpush key value1 [value2...]		# 将多个值插入到列表头部
   lpushx key value	# 将值插入到已存在的列表头部
   lpush key value1 [value2...]		# 将多个值插入到列表尾部
   lpushx key value	# 将值插入到已存在的列表尾部
   lpop key 			# 获取并移除列表的第一个元素
   rpop key 			# 获取并移除列表的最后一个元素
   rpoplpush source dest		# source的列表尾弹出元素，插入到dest列表中
   llen key 			# 获取列表长度
   linsert key before|after element value	# 在列表指定元素的前|后插入元素
   lindex key index	# 获取列表索引位的元素
   lset key index value		# 将列表索引位的元素设置为新值
   lrange key start stop 		# 获取列表指定范围内的元素
   lrem key count value		# 移除最多count个指定元素
   blpop key [key2...] timeout	# 获取并移除列表的第一个元素（阻塞）
   brpop key [key2...] timeout	# 获取并移除列表的最后一个元素（阻塞）
   brpoplpush source dest timeout	# source的列表尾弹出元素，插入到dest列表中（阻塞）
   
   ```

   

3. 集合类型（Set）

   ```shell
   # Redis管理集合的基本命令
   
   ```



4. 有序集合（sorted set）

   ```shell
   # Redis管理有序集合的基本命令
   ```

   

5. Hash数据结构

   redis hash是一个string类型的field和value的映射表，特别适合用于存储对象。

   ![](F:\world\学习笔记\pic\redis hash.png)

   hash的基本操作和redis的字符串类型基本操作相似：

   ```shell
   hkeys key			# 获取键的所有字段
   hexists key field	# 判断键是否存在特定字段
   hdel key field [field2...]		# 删除键的特定字段
   hget key field		# 获取键的特定字段的值
   hgetall key			# 获取键的所有字段的值
   hincrby key field increament	# 将键的特定字段的整数值增加特定数
   hset key field value			# 设置键的字段值
   ...
   ```

   

## 三、生存时间

1. 设置生存时间

   ```shell
   expire key time	# 设置key的生存时间(s)
   ttl key			# 查看key的生存时间(s),返回值：大于0，剩余生存时间；-1，没有生存时间，永久存储；-2，数据已经删除
   ```

   

2. 清除生存时间

   ```shell
   persist key		# 清除key的生存时间，永久存储。
   				# 重新设置值也会清除生存时间
   ```

   

## 四、事务

​	redis事务可以一次执行多个命令，并且带有一下两个重要的保证：

- 事务是一个单独的隔离操作：事务中的所有命令都会序列化、按顺序地执行。事务在执行的过程中不会被其他客户端发送的命令请求打断；

- 事务是一个原子操作：事务中的命令要么全部被执行，要么全部都不执行。

  

```shell
# Redis事务命令
discard		# 取消事务，放弃执行事务块内的所有命令
exec		# 执行事务块内的所有命令
multi		# 标记一个事务块的开始
watch key [key1...]		# 监视keys，如果事务执行之前这些keys被其他命令改动，那么事务将被打断
unwatch 	# 取消watch命令对所有key的监控
```

