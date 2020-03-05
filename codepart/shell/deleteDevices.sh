#!/bin/bash

####################################################
##	
## author:zhengzebiao		
## date:2019-12-13
##
## 读取文件中的内容，使用curl循环发送请求
####################################################

base_dir=$(cd `dirname $0`;  pwd)

filename=$base_dir/illegalDeviceId.txt


while read line 
do 
  query="{\"query\":{\"term\":{\"DeviceID\":\"ORIGINALVALUE\"}}}"
  # 将以空格分隔的字符串转换成数组
  array=($line)
  query=${query/ORIGINALVALUE/${array[0]}}

  curl -XPOST --tlsv1.2 --negotiate -k -u : 'https://68.29.243.207:24100/tb_face-*/_delete_by_query?pretty' -H 'Content-Type: application/json' -d $query
done<$filename

echo "operation is completed!"
