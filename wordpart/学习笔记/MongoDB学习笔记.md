# MongoDB学习笔记

## 1. MongoDB含义及其特性

### 1.1 简介

1. 易于使用
   1. 是一个面向文档的数据库；
   2. 使用BSON作为数据存储和传输的格式；
   3. 没有预定义模式（no schema）
2. 易于扩展
   1. 设计上采用横向扩展；
   2. 面向文档的数据模型使多台机器之间的数据分隔更加容易；
   3. 自动处理跨集群的数据和负载，自动分配文档；
   4. 路由机制使用户请求能发送到正确的机器上。
3. 丰富的功能
   1. 支持多类型索引；
   2. 支持聚合管道操作；
   3. 支持特殊的集合类型，支持大文件存储。
4. 卓越的性能
   1. 对文档进行动态填充；
   2. 用尽可能多的内存用作缓存

### 1.2 数据类型

 	1. 基本数据类型：
      	1. null
      	2. 布尔型：true/false
      	3. 数值类型
      	4. 字符串类型
      	5. 日期类型：new Date()
      	6. 正则表达式
      	7. 数组类型 : {"things":["pie", 3.14]}
      	8. 内嵌文档类型: {"address": {"street": "123 Park Street"， “city": "Anytown", "state": "NY"}}
      	9. 对象id: ObjectId(4时间戳 + 3主机标识 + 2PID + 3自动计数器)
      	10. 二进制数据:
      	11. 代码

### 1.3 与关系型数据库MySQL的对比

| **MySql**    | **Mongodb**    |
| ------------ | -------------- |
| database     | database       |
| table        | collection     |
| index        | index          |
| row          | bson  document |
| column       | bson  field    |
| primary  key | _id            |
| group  by    | aggregation    |

## 2. MongoDB自带的工具

## 3. MongoDB shell

## 4. 查询语法

### 4.1 基本查询语法

```shell
# 启动mongodb
./mongod --dbpath /data/db --port 27017
# 连接上mongodb
./mongo localhost:21017
# 查看数据库
show dbs
# 连接test数据库
use test
# 查看数据库中集合
show collections
# 增删改查
db.user.insert({_id:0,name:'tom',age:20})
db.user.remove({_id:0})
db.user.update({_id:0},{$set:{sex:'male'}})	//_id为0的文档增加sex字段
db.user.update({_id:0},{$inc:{age:1}})	//_id为0的文档age自加1
db.user.update({_id:0},{$unset:{sex:1}})
db.user.update({_id:0},{$push:{hobby:'football'}})
db.user.update({_id:0},{$pop:{hobby:1}})
db.user.update{{_id:0},{$pull:hobby:'football'}}

#select age from user where name='tom' and age >= 12 limit m,n order by age
# $gte $gt $lte $lt $ne $in $nin $exists $or $nor 
db.user.find({age:{$gte:12},name:'tom'},{_id:0,age:1}).sort({age:1}).skip(m).limit(n-m)
```



### 4.2 复杂聚合查询

#### 1.聚合管道

```shell
 
 db.collection.aggregate()是基于数据处理的聚合管道，每个文档通过一个由多个阶段（stage）组成的管道，可以对每个阶段的管道进行分组、过滤等功能，然后经过一系列的处理，输出相应的结果。
这里介绍一下聚合框架中常用的几个操作：
    $project：修改输入文档的结构。可以用来重命名、增加或删除域，也可以用于创建计算结果以及嵌套文档。
    $match：用于过滤数据，只输出符合条件的文档。$match使用MongoDB的标准查询操作。
    $limit：用来限制MongoDB聚合管道返回的文档数。
    $skip：在聚合管道中跳过指定数量的文档，并返回余下的文档。
    $unwind：将文档中的某一个数组类型字段拆分成多条，每条包含数组中的一个值。
    $group：将集合中的文档分组，可用于统计结果。
    $sort：将输入文档排序后输出。
    $geoNear：输出接近某一地理位置的有序文档。
    
$project:{"_id":0,"name":1}
	1. 数字表达式
	"$add":["$age",1]
	"$substract":["$age",1]
	"$multiply":["$age",1.5]
	"$divide":["$age",1.5]
	"$mod":["$age",3]
	2. 日期表达式
	$year,$month,$week,$dayOfMonth,$dayOfWeek,$dayOfYear,$hour,$minute,$second
	3. 字符串表达式
	"$substr":[expr,startOffset,numToReturn]
	"$concat":[expr...]
	"$toLower":expr
	"$toUpper":expr
	4. 逻辑表达式
	$cmp,$strcasecmp,$eq/$ne/$gt/$gte/$lt/$lte,$and,$or,$not,$cond:[booleanExpr,trueExpr,falseExpr],$ifNull:[expr,replacementExpr]
	
$group:{"_id":"$day"}
	1. 算术操作符
	$sum,$avg
	2. 极值操作符
	$max,$min,$first,$last
	3. 数组操作符
	$addToSet,$push
	
# MongoDB不允许单一的聚合操作占用过多的系统内存：如果MongoDB发现某个聚合操作占用了20%以上的内存，这个操作就会直接输出错误。
```



#### 2.MapReduce

```shell
# Example:
var mapFunction1 = function() {
   emit(this.cust_id, this.price);
};
var reduceFunction1 = function(keyCustId, valuesPrices) {
   return Array.sum(valuesPrices);
};
db.orders.mapReduce(
   mapFunction1,
   reduceFunction1,
   { out: "map_reduce_example" }
)
```





### 4.3 写入安全机制（Write Concern）



## 5. 索引

1. 单键索引 db.user.createIndex( {age: 1} ) // 1: 升序、-1:降序 

   在一个键上创建的索引就是单键索引，单键索引是最常见的索引，如MongoDB默认创建的_id的索引就是单键索引。

2. 复合索引 db.user.createIndex( {name: 1, age: 1} ) 

   在多个键上建立的索引就是复合索引，查询与索引方向不同的话，可能无法使用复合索引。

3. 多建索引

   如果在一个值为数组的字段上面创建索引， MongoDB会自己决定，是否要把这个索引建成多键索引

4. 地理空间索引 db.user.createIndex({w:”2d”})

   MongoDB支持几种类型的地理空间索引。其中最常用的是 2dsphere 索引（用于地球表面类型的地图）和 2d 索引（用于平面地图和时间连续的数据）

5. 全文索引 db.user.createIndex({name:"text"})

   全文索引用于在文档中搜索文本，我们也可以使用正则表达式来查询字符串，但是当文本块比较大的时候，正则表达式搜索会非常慢，而且无法处理语言理解的问题（如 entry 和 entries 应该算是匹配的）。使用全文索引可以非常快地进行文本搜索，就如同内置了多种语言分词机制的支持一样。创建索引的开销都比较大，全文索引的开销更大。创建索引时，需后台或离线创建

6. 哈希索引 db.user.createIndex( { name: "hashed" } )

   哈希索引可以支持相等查询，但是 哈希 索引不支持范围查询。您可能无法创建一个带有 哈希 索引键的复合索引或者对 哈希 索引施加唯一性的限制。但是，您可以在同一个键上同时创建一个 哈希 索引和一个递增/递减(例如，非哈希)的索引，这样MongoDB对于范围查询就会自动使用非哈希的索引

```java
- 索引额外属性
MongoDB除了支持多种不同类型的索引，还能对索引定制一些特殊的属性。
db.collection.createIndex({indexValue},{indexProperty})
1. 唯一索引 (unique index)：保证索引对应的字段不会出现相同的值，比如_id索引就是唯一索引
db.user.createIndex({'name':1},{unique:true)
2. TTL索引：可以针对某个时间字段，指定文档的过期时间（经过指定时间后过期 或 在某个时间点过期）
db.user.createIndex({'time':1}, {expireAfterSeconds: 30}) //30s删除
3. 部分索引 (partial index): 只针对符合某个特定条件的文档建立索引，3.2版本后支持该特性
db.user.createIndex({'name':1},{partialFilterExpression: {'age': {$gt:25}}})//基于age列创建大于25岁的部分索引
4. 稀疏索引(sparse index): 只针对存在索引字段的文档建立索引，可看做是部分索引的一种特殊情况
db.user.createIndex({'email': 1}, { sparse: true})
```



## 6. 副本集和分片集群搭建

