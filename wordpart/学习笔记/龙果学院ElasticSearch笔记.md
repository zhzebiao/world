# 龙果学院ElasticSearch笔记

# 一、单字段检索

## 7. 手动控制全文检索结果的精准度

1. 对于type=text字段使用match query进行全文检索

      ```shell
      # 搜索标题中包含java或elasticsearch的记录
      GET /forum/_search
      {
        "query": {
          "match": {
            "title": "java elasticsearch"		#满足越多值的记录分数越高，排名越前
          }
        }
      }
      
      # 搜索标题中包含多个分词的记录
      GET /forum/_search
      {
        "query": {
          "match": {
            "title": {
              "query": "java elasticsearch python hadoop",
              "operator": "and",					#需要全部满足
              # "minimum_should_match": "75%" 	#只需要满足3/4的记录
            }
          }
        }
      }
      ```

      

2. ```shell
      GET /forum/_search
      {
        "query": {
          "bool": {
            "must": [
              {"match": {"title": "java"}}
            ],
            "must_not": [
              {"match": {"title": "python"}}
            ],
            "should": [
              {"match": {"title": "hadoop"}},
              {"match": {"title": "elasticsearch"}}
            ]
          }
        }
      }
      
      GET /forum/_search
      {
        "query": {
          "bool": {
            "should": [
              {"match": {"title": "java"}},
              {"match": {"title": "elasticsearch"}},
              {"match": {"title": "hadoop"}},
              {"match": {"title": "python"}}
            ],
            "minimum_should_match": 3
          }
        }
      }
      ```

      relevance score = （must score + should score）/（must count + should count）

3. 总结：

     ​		elasticsearch中的text类型数据在分词之后，会被分成多个term插入到倒排索引中。在实际检索中可以用多词检索的方法去检索。所以，match query在底层实际上会转换成bool + term的检索语法。



# 二、多字段检索

## 11. 实现best fields策略进行多字段搜索

1. 普通的match should请求

   ```shell
   GET /forum/_search
   {
     "query": {
       "bool": {
         "should": [
           {"match":{"title":"java solution"}},
           {"match":{"content":"java solution"}}
         ]
       }
     }
   }
   ```

   ​	ES在计算relevance score的时候，遵循TF/IDF原则，越匹配的score值越大。有时候会出现计算结果和人为认知结果不同。例如：

   ​	doc4：{"title":"this is a java program","content":"this is java content"} 

   ​	score：（1.1+1.1）* 2  / 2 = 2.2

   ​	doc5：{"title":"this is a spark program","content":"this is a java solution"}

   ​	score：（0 + 2.2）* 1  / 2 = 1.1

   程序结果 doc4 > doc5，但人为认知doc5的content中有匹配度更高的java solution，应该是doc5 > doc4。

2. best fields策略——dis_max

      best_fields策略：搜索到的结果是某一个field中匹配到最多的关键词的记录；

      dis_max语法：将多个query中分数最高的query的分数当做总分数返回。

      ```shell
      GET /forum/_search
      {
        "query": {
          "dis_max": {
            "tie_breaker": 0.7,				#将其他query的分数*tie_breaker计入总分数
            "boost": 1.2,						#增加权重
            "queries": [
              {"match":{"title":"java solution"}},
              {"match":{"content":"java solution"}}
              ]
          }
        }
      }
      ```

3. 基于multi_match语法实现dis_max+tie_breaker

      ```bash
      GET /forum/_search
      {
      "query": {
            "multi_match": {
                "query":                "java solution",
                "type":                 "best_fields", 
                "fields":               [ "title^2", "content" ],
                "tie_breaker":          0.3,	# 其他query权重
                "minimum_should_match": "50%"	# 至少匹配个数
            }
          } 
        }
      ```

        

## 14. 基于multi_match+most fiels策略进行多字段搜索

1. 首先，定义一个分词字段，用了不同的分词器分词

   ```bash
   POST /forum/_mapping/
   {
     "properties": {
         "sub_title": { 
             "type":     "text",
             "analyzer": "english",
             "fields": {
                 "std":   { 
                     "type":     "text",
                     "analyzer": "standard"
                 }
             }
         }
     }
   }
   
   # 插入两条doc
   POST /forum/article/_bulk
   { "update": { "_id": "1"} }
   { "doc" : {"sub_title" : "learning more courses"} }
   { "update": { "_id": "2"} }
   { "doc" : {"sub_title" : "learned a lot of course"} }
   
   # 进行检索的时候，发现_id为1的doc评分比_id为2的低，与期望不符
   # 原因是English分词器会将单词还原为最基本形态，stemmer
   GET /forum/article/_search
   {
     "query": {
       "match": {
         "sub_title": "learning courses"
       }
     }
   }
   ```

   

2. 采用most_fields

   ```bash
   # standard分词器不会还原单词。增加std字段，进行多字段检索，能够提高_id为1的记录的评分
   GET /forum/_search
   {
      "query": {
           "multi_match": {
               "query":  "learning courses",
               "type":   "most_fields", 
               "fields": [ "sub_title", "sub_title.std" ] 
           }
       }
   }
   
   ```
   
3. 总结

   most_fields策略：尽可能有限返回更多的field匹配到某个关键词的doc。在上面的例子中是通过增加standard分词的字段，让_id为1的doc匹配到的记录更多，以此增加其评分。

   与best_fields策略不同，best_fields策略关注的是查询词在单字段中的匹配率，most_fields策略关注的是查询词在多字段中的总体命中率。

   

## 16. 使用copy_to/cross-fields策略解决cross-fields搜索弊端

1. 弊端原因

   cross-fields：一个唯一标识，跨了多个field。例如一个人的姓名被定义为first name和second name两个字段，而这两个字段的组合才能完全定位到一个人。可以使用多字段检索中的most_fields策略检索，但会存在检索弊端。`（不用使用best_fields，想一想为什么？）`

   如果使用most_field去匹配，会出现许多弊端

   ​	① most_fields只是找到尽可能多的fields匹配的doc，而不是某个field完全匹配的doc；

   ​	② most_fields没有办法像best_fields一样，没办法使用minimum_should_match去掉长尾`（匹配率非常低的doc）`

   ​	③ 底层TF/IDF算法，底层检索细节不明，可能会将一些特殊清况计算出一个更高的分数。`（例如在检索Peter Smith的时候，Smith Williams反而会排在Peter Smith前面）`

2. 使用copy_to将多个field组合成一个field

   ```bash
   PUT /forum/_mapping/article
   {
     "properties": {
         "new_author_first_name": {
             "type":     "text",
             "copy_to":  "new_author_full_name" 
         },
         "new_author_last_name": {
             "type":     "text",
             "copy_to":  "new_author_full_name" 
         },
         "new_author_full_name": {
             "type":     "text"
         }
     }
   }
   ```

   

3. 使用multi_match+cross_fields策略

   ```bash
   GET /forum/_search
   {
     "query": {
       "multi_match": {
         "query": "Peter Smith",
         "type": "cross_fields", 
         "operator": "and",		# 每个term至少在一个field中出现
         "fields": ["author_first_name", "author_last_name"]
       }
     }
   }
   ```

   

# 三、近似匹配

## 18. phrase matching搜索技术

1. 普通match query

   ```shell
   GET /forum/_search
   {
   	"match": {
   		"content": "java spark"
   	}
   }
   ```

   普通的match query只能搜索到包含java、spark的doc，但是没办法知道java和spark是不是离得近。如果有需求：

   ​	① 检索content中java spark靠在一起的、中间不能有其他字符的doc；

   ​	② 检索content中包含java spark的doc，java和spark两个单词靠得越近，排名越前。

   这样普通的match query就没办法实现

2. match_phrase 短语匹配

   ```shell
   GET /forum/_search
   {
       "query": {
           "match_phrase": {
               "content": {
               	"query":"java spark",
               	"slop":1			# 允许查询的term进行移动slop位，与doc相匹配
               }
           }
       }
   }
   ```

   通过match_phrase+slop就可以实现多个term的位置关系匹配。

3. 混合使用match和近似匹配实现召回率与精准度

   召回率：返回的doc占所有doc的比率称为召回率；

   精准度：尽可能让包含查询term，或者是查询term距离越近的doc排在越前面；

   单独使用普通match的时候，查询精准度会比较低。单独使用match_phrase的时候，只有满足包含所有查询term的条件的doc才会返回，召回率比较低。

   ```bash
   # 混合使用普通match和match_phrase
   GET /forum/_search
   {
     "query": {
       "bool": {
         "must": {
           "match": { 
             "title": {
               "query":"java spark"	# java或spark或java spark，java和spark靠前，但是没法区分java和spark的距离，也许java和spark靠的很近，但是没法排在最前面
             }
           }
         },
         "should": {
           "match_phrase": {			# 在slop以内，如果java spark能匹配上一个doc，那么就会对doc贡献自己的relevance score，如果java和spark靠的越近，那么就分数越高
             "title": {
               "query": "java spark",
               "slop":  50
             }
           }
         }
       }
     }
   }
   ```

   

## 19. 使用rescoring机制优化近似匹配搜索的性能

1. 普通match和match_phrase的区别

   普通match只要匹配到doc中是否包含查询term就可以通过TF/IDF计算结果返回。match_phrase匹配doc的时候会更复杂一些，需要先查询doc是否包含查询term，然后计算查询term在doc中的位置是否满足指定返回，最后需要进行复杂的slop移动，判断能否通过移动slop匹配到doc。

   在性能上面，普通match的性能要比match_phrase的性能高10倍~20倍。

2. 优化思路

   1. 混合使用match和match_phrase，只对满足match条件的doc进行match_phrase运算

   2. 可以只对部分数据进行match_phrase运算，设定window size，对满足match条件的topN条记录进行rescore

   ```bash
   GET /forum/_search 
   {
     "query": {
       "match": {
         "content": "java spark"
       }
     },
     "rescore": {
       "window_size": 50,
       "query": {
         "rescore_query": {
           "match_phrase": {
             "content": {
               "query": "java spark",
               "slop": 50
             }
           }
         }
       }
     }
   }
   ```
   
   

## 22. 实战前缀搜索、通配符搜索、正则搜索技术

​	上面讲到的普通match和match_phrase都是对type=text的字段进行查询检索，本节讲解的技术**只适用于type=keyword**的字段。type=keyword的字段不进行分词，在进行前缀搜索、通配符搜索、正则搜索的时候，都要去扫描所有的倒排索引才能返回结果，**性能非常差**，在实战中尽量避免频繁使用。

1. 前缀搜索

   前缀越短，要处理的doc越多，性能越差。

   ```bash
   GET /my_index/_search
   {
     "query": {
       "prefix": {
         "title": {
           "value": "C3"
         }
       }
     }
   }
   # 查询出来的doc score都是1 => prefix不进行score运算
   ```

   

2. 通配符搜索

   ```bash
   GET my_index/_search
   {
     "query": {
       "wildcard": {
         "title": {
           "value": "C?K*5"
         }
       }
     }
   }
   ```

   

3. 正则搜索

   ```bash
   GET /my_index/_search 
   {
     "query": {
       "regexp": {
         "title": "C[0-9].+"
       }
     }
   }
   ```

   

## 23. 使用match_phrase_prefix实现search-time搜索推荐

​	在有实时搜索需求的场景下，可以使用match_phrase_prefix进行实时搜索推荐。`（例如：用百度搜索 'elas' 的时候会实时显示出 'elasticsearch' 的结果项）`

1. 示例

   ```bash
   GET /my_index/_search
   {
     "query": {
       "match_phrase_prefix": {
         "title": {
           "query": "hello w",
           "slop":2,
           "max_expansions": 2
         }
       }
     }
   }
   ```

   

2. 原理解析
   1. hello就是去进行match，搜索对应的doc。w会作为前缀，去扫描整个倒排索引，找到所有w开头的term
   2. 根据你的slop去计算，看在slop范围内，能不能让hello w，正好跟doc中的hello和w开头的单词的position相匹配
   3. 也可以指定slop，但是只有最后一个term会作为前缀
   4. max_expansions：指定prefix最多匹配多少个term，超过这个数量就不继续匹配了，限定性能。



## 24. 使用ngram分词机制实现index-time搜索推荐

1. 什么是ngram

   ```html
   quick，5种长度下的ngram
   
   ngram length=1，q	u	i	c	k
   ngram length=2，qu	ui	ic	ck
   ngram length=3，qui	uic	ick
   ngram length=4，quic	uick
   ngram length=5，quick
   ```

   edge ngram filter是ngram filter的特殊实现，能够满足前缀搜索需要。与ngram filter不同的是，edge ngram filter只会输出那些以token首字母开头的ngrams。

   ```bash
   GET _analyze
   {
     "tokenizer": "standard",
     "filter": [
       { "type": "edge_ngram",
         "min_gram": 1,
         "max_gram": 2
       }
     ],
     "text": "the quick brown fox jumps"
   }
   # 输出结果为：[ t, th, q, qu, b, br, f, fo, j, ju ]
   ```

   

2. 实战ngram

   ```bash
   # 建立索引，定义索引内的自定义分词器，分词器中的filter使用edge ngram filter
   PUT /my_index
   {
       "settings": {
           "analysis": {
               "filter": {
                   "autocomplete_filter": { 
                       "type":     "edge_ngram",
                       "min_gram": 1,
                       "max_gram": 20
                   }
               },
               "analyzer": {
                   "autocomplete": {
                       "type":      "custom",
                       "tokenizer": "standard",
                       "filter": [
                           "lowercase",
                           "autocomplete_filter" 
                       ]
                   }
               }
           }
       }
   }
   # 设置title字段类型，索引的时候使用自定义分词器分词，检索的时候使用standard分词器分词，能够减少查询的token数量
   PUT /my_index/_mapping/my_type
   {
     "properties": {
         "title": {
             "type":     "text",
             "analyzer": "autocomplete",
             "search_analyzer": "standard"
         }
     }
   }
   ```

   

## 29. 掌握误拼写时的fuzzy模糊搜索技术

1. 搜索的时候，可能输入的搜索文本会出现错误拼写的情况，使用fuzzy进行纠正。

   ```shell
   GET /my_index/_search 
   {
     "query": {
       "fuzzy": {
         "text": {
           "value": "surprize",
           "fuzziness": 2			#最大纠正的错误数，default：2
         }
       }
     }
   }
   ```

   ```shell
   # 更多使用下面这种用法
   GET /my_index/_search 
   {
     "query": {
       "match": {
         "text": {
           "query": "SURPIZE ME",
           "fuzziness": "AUTO",
           "operator": "and"
         }
       }
     }
   }
   ```

   

# 四、相关系数评分解析

## 25. 深入揭秘TF&IDF算法以及向量空间模型算法

1. TF/IDF算法以及vector space model解析

   1. boolean model

      先过滤出包含指定term的doc，只返回true / false，不计算score，减少后续计算的doc数量，提升性能。

   2. 每个term进行TF/IDF运算

      TF`（term frequency）`： 计算term在doc中出现的次数，出现次数越多，TF评分越高；

      IDF`（inversed document frequency）`：计算term在所有doc中出现的次数，出现的次数越多，IDF评分越低；
   
   length norm：doc中用于搜索的field长度，field长度越长，length norm评分越低；
   
   3. vector space model
   
      将多个term的TF/IDF分数组成一个vector space，[term1 score, term2 score ... termN score]，这个向量值就是这个doc的总体评分

2. lucene的相关度分数算法

   practical scoring function

   ```shell
   score(q,d)  =  
               queryNorm(q)  		# 归一化函数
             · coord(q,d)			# 奖励函数 ，让更加匹配的doc的分数获得成倍奖励   
             · ∑ (           
                   tf(t in d)   
                 · idf(t)2      
                 · t.getBoost() 
                 · norm(t,d)    
               ) (t in q) 
   ```

   $$
   score(q,d)  =  
               queryNorm(q)  
             · coord(q,d)    
             · ∑ (           
                   tf(t in d)   
                 · idf(t)2      
                 · t.getBoost() 
                 · norm(t,d)    
               ) (t in q)
   $$

   

## 27. 常见的相关度分数优化方法

1. query-time boost

   ```shell
   GET /forum/_search
   {
     "query": {
       "bool": {
         "should": [
           {
             "match": {
               "title": {
                 "query": "java spark",
                 "boost": 2			# 增加title字段的查询权重
               }
             }
           },
           {
             "match": {
               "content": "java spark"
             }
           }
         ]
       }
     }
   }
   ```

   

2. 重构查询结构`（新版本中影响越来越小）`

   ```shell
   GET /forum/_search 
   {
     "query": {
       "bool": {
         "should": [
           {
             "match": {
               "content": "java"
             }
           },
           {
             "match": {
               "content": "spark"
             }
           },
           {
             "bool": {
               "should": [
                 {
                   "match": {
                     "content": "solution"
                   }
                 },
                 {
                   "match": {
                     "content": "beginner"
                   }
                 }
               ]
             }
           }
         ]
       }
     }
   }
   ```

   

3. negative boost

   搜索包含java，尽量不包含spark的doc，但不排查掉含有spark的doc。包含了negative term的doc，分数乘以negative boost，降低评分分数。

   ```shell
   GET /forum/_search 
   {
     "query": {
       "boosting": {
         "positive": {
           "match": {
             "content": "java"
           }
         },
         "negative": {
           "match": {
             "content": "spark"
           }
         },
         "negative_boost": 0.2		# 包含negative term的doc，分数降低的比率
       }
     }
   }
   ```

   

4. constant_score`（查询结果不评分）`

   ```shell
   GET /forum/article/_search 
   {
     "query": {
       "bool": {
         "should": [
           {
             "constant_score": {
               "query": {
                 "match": {
                   "title": "java"
                 }
               }
             }
           },
           {
             "constant_score": {
               "query": {
                 "match": {
                   "title": "spark"
                 }
               }
             }
           }
         ]
       }
     }
   }
   ```

   

## 28. 自定义相关度分数算法

1. 自定义function score，将查询分数与其他字段相关联

   ```shell
   GET /forum/_search
   {
     "query": {
       "function_score": {
         "query": {
           "multi_match": {
             "query": "java spark",
             "fields": ["tile", "content"]
           }
         },
         "field_value_factor": {
           "field": "follower_num",
           "modifier": "log1p",
           "factor": 0.5
         },
         "boost_mode": "sum",
         "max_boost": 2
       }
     }
   }
   ```

   $$
   newScore = oldScore + min(log(1 + factor * followerNum),maxBoost)
   $$

   

# 五、聚合数据分析

## 33.bucket metric基本使用

1. bucket与metric概念讲解

   bucket：一个数据分组；

   metric：对一个数据分组执行的统计；

2. 聚合统计：

   ```shell
   # 根据颜色分组（=count(color)）
   GET /tvs/_search
   {
     "size": 0,
     "aggs": {
       "popular_colors": {
         "terms": {
           "field":"color"
         }
       }
     }
   }
   
   # 根据颜色分组，并计算每个分组的平均价格（=avg(price) group by(color)）,最高价格（=max(price) group by(color)），最低价格（=min(price) group by(color)），价格总和（=sum(price) group by(color)）
   GET /tvs/_search
   {
     "size": 0,
     "aggs": {
       "colors": {
         "terms": {"field":"color"},
         "aggs":{
           "avg_price":{"avg":{"field":"price"}},
           "min_price":{"min":{"field":"price"}},
           "max_price":{"max":{"field":"price"}},
           "sum_price":{"sum":{"field":"price"}}
         }
       }
     }
   }
   
   # 直方图统计,以2000为统计区间，统计每个区间的价格总和
   GET /tvs/_search
   {
     "size": 0,
     "aggs": {
       "price": {
         "histogram": {
           "field":"price",
           "interval":2000			# 分组间隔
         },
         "aggs":{
           "revenue":{
             "sum":{
               "field":"price"
             }
           }
         }
       }
     }
   }
   
   # 日期直方图
   #  Calendar intervals can only be specified in "singular" quantities of the unit (1d, 1M, etc). Multiples, such as 2d, are not supported and will throw an exception
   GET /tvs/_search
   {
   	"size" : 0,
   	"aggs" : {
   		"sales" : {
   			"date_histogram" : {
   				"field" : "sold_date",
   				"calendar_interval" : "month",
   				"format" : "yyyy-MM-dd",
   				"min_doc_count" : 0,
   				"extended_bounds" : {
   					"min" : "2016-01-01",
   					"max" : "2017-12-31"
   				}
   			}
   		}
   	}
   }
   # In contrast to calendar-aware intervals, fixed intervals are a fixed number of SI units and never deviate, regardless of where they fall on the calendar.This allows fixed intervals to be specified in any multiple of the supported units.
   GET /tvs/_search
   {
     "size": 0,
     "aggs":{
       "sales":{
         "date_histogram":{
           "field":"sold_date",
           "fixed_interval":"30d",
           "format":"yyyy-MM-dd",
           "min_doc_count":0,
           "extended_bounds":{
             "min":"2016-01-01",
             "max":"2017-12-31"
           }
         }
       }
     }
   }
   
   ```
   
   

## 41. 搜索和聚合结合使用

1. 对查询结果的scope之中进行聚合操作

   ```shell
   GET /tvs/_search
   {
     "size": 0,
     "query":{
       "term": {
         "brand": {
           "value": "小米"
         }
       }
     },
     "aggs": {
       "group_by_color": {
         "terms": {
           "field":"color"
         }
       }
     }
   }
   ```

   

2. 在查询结果的范围中对全局数据进行聚合统计

   global ： global bucket，将所有数据纳入聚合的scope中

   ```shell
   GET /tvs/_search
   {
     "size": 0,
     "query":{
       "term": {
         "brand": {
           "value": "长虹"
         }
       }
     },
     "aggs": {
       "single_avg_price": {
         "avg": {
           "field":"price"
         }
       },
       "all":{
         "global":{},		# 重置scope为全局scope
         "aggs":{
           "avg_price":{
             "avg":{
               "field":"pric"
             }
           }
         }
       }
     }
   }
   ```

   

3. 统计前进行scope限定，通过filter限定scope范围

   ```
   GET /tvs/_search
   {
     "size": 0,
     "query": {
       "term": {
         "brand": {
           "value": "长虹"
         }
       }
     },
     "aggs": {
       "recent_one_month": {
         "filter":{
           "range":{
             "sold_date":{
               "gte":"now-1200d"
             }
           }
         },
         "aggs": {
           "avg_price":{
             "avg":{
               "field":"price"
             }
           }
         }
       }
     }
   }
   ```

   

## 44.下钻分析中的排序问题

1. 按颜色的平均销售额升序排序

   ```shell
   GET /tvs/_search
   {
     "size": 0,
     "aggs": {
       "group_by_color": {
         "terms": {
           "field":"color",
           "order":{
             "avg_price":"asc"		# 重设排序规则
           }
         },
         "aggs":{
           "avg_price":{
             "avg":{
               "field":"price"
             }
           }
         }
       }
     }
   }
   ```

   

## 47.近似聚合算法(cardinality + percentiles + percentile ranks)

三角选择原则

1. 精准+实时 ： 数据量不大，一般数据统计可以在单机运行得出结果
2. 精准+大数据 ： hadoop等批处理操作，精准但延时非常大
3. 大数据+实时： 近似估计，可能会有百分之几的错误率

近似估计算法

1. cartinality去重近似估计算法

   ```
   GET /tvs/_search
   {
     "size": 0,
     "aggs": {
       "months": {
         "date_histogram": {
           "field":"sold_date",
           "calendar_interval":"month",
           "format":"yyyy-MM-dd"
         },
         "aggs":{
           "distinct_colors":{
             "cardinality":{
               "field":"brand"
             }
           }
         }
       }
     }
   }
   ```

   算法优化内存开销以及HLL算法：

   ​	precision_threshole：如果数据的unique value小于precision_threshole，那么cardinality几乎能够保证100%准确；并且只会占用precision_threshole * 8 byte的内存消耗`（precision_threshole值可以调整得更大）` ；就算实际数据有数百万个unique value，统计的错误率只会在5%以内；

   ```shell
   GET /tvs/_search
   {
     "size": 0,
     "aggs": {
       "distinct_brand": {
         "cardinality": {
           "field":"brand",
           "precision_threshold":100
         }
       }
     }
   }
   ```

   ​	HyperLogLog++算法优化：HLL算法是cardinality的底层算法。默认情况下，发送一个cardinality请求的时候，会**动态地对所有field value取hash值**；如果对cardinality的性能有比较高的要求，可以**将取hash值的操作前移到建立索引的时候**。

   ```shell
   PUT /tvs
   {
     "mappings": {
       "properties": {
         "brand":{
           "type": "text",
           "fields": {
             "hash":{
               "type":"murmur3"		# 一种取hash值的方法
             }
           }
         }
       }
     }
   }
   
   GET /tvs/_search
   {
     "size": 0,
     "aggs": {
       "distinct_brand": {
         "cardinality": {
           "field":"brand.hash",		# cardinality的时候使用hash字段
           "precision_threshold":100
         }
       }
     }
   }
   ```

   

2. percentiles百分比算法以及网站访问时延统计

   ```shell
   # 对latency字段进行tp50,tp90,tp99计算
   GET /website/_search
   {
     "size": 0,
     "aggs": {
       "latency_percentiles": {
         "percentiles": {
           "field":"latency",
           "percents":[
             50,
             90,
             99
             ]
         }
       },
       "latency_avg":{
           "avg":{
             "field":"latency"
           }
       }
     }
   }
   
   # 对每个省份进行tp50，tp90，tp99统计
   GET /website/_search
   {
   	"size": 0,
   	"aggs": {
   		"grouy_by_province": {
   			"terms": {
   				"field": "province"
   			},
   			"aggs": {
   				"latency_percentiles": {
   					"percentiles": {
   						"field": "latency",
   						"percents": [
   							50,
   							90,
   							99
   						]
   					}
   				},
   				"latency_avg": {
   					"avg": {
   						"field": "latency"
   					}
   				}
   			}
   		}
   
   	}
   }
   ```

   

3. percentile ranks以及网站访问时延SLA统计

   SLA ： 提供的服务的标准。

   我们的网站的提供的访问延时的SLA，确保所有的请求100%在200ms以内。

   ```shell
   GET /website/_search
   {
     "size": 0,
     "aggs": {
       "group_by_province": {
         "terms": {
           "field":"province"
         },
         "aggs":{
           "lantency_percentile_ranks":{
             "percentile_ranks":{
               "field":"latency",
               "values":[200,1000]
             }
           }
         }
       }
     }
   }
   ```

   