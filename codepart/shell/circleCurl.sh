#!/bin/bash

####################################################
##	
## author:zhengzebiao		
## date:2019-12-13
##
## 读取文件中的内容，使用curl循环发送请求
####################################################

filename="/home/test"

updateQuery="curl -XPOST --tlsv1.2 --negotiate -k -u : 'INDEXURL/_update_by_query?pretty' -H 'Content-Type: application/json' -d '{\"query\":{\"term\":{\"DeviceID\":\"ORIGINALVALUE\"}},\"script\":{\"source\":\"ctx._source['DeviceID'] = 'MODIFIEDVALUE'\"}}'"
deleteQuery="curl -XPOST --tlsv1.2 --negotiate -k -u : 'INDEXURL/_delete_by_query?pretty' -H 'Content-Type: application/json' -d ' {\"query\":{\"term\":{\"DeviceID\":\"ORIGINALVALUE\"}}}'"

while read line 
do 
  query="{\"query\":{\"term\":{\"DeviceID\":\"ORIGINALVALUE\"}},\"script\":{\"source\":\"ctx._source['DeviceID']='MODIFIEDVALUE'\"}}"
  # 将以空格分隔的字符串转换成数组
  array=($line)
  query=${query/ORIGINALVALUE/${array[0]}}
  query=${query/MODIFIEDVALUE/${array[1]}}

  curl -XPOST --tlsv1.2 --negotiate -k -u : 'https://71.178.56.186:24100/tb_face-*/_update_by_query?pretty' -H 'Content-Type: application/json' -d $query
done<$filename

echo "operation is completed!"
