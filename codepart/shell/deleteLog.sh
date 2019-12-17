#!/bin/bash
####################################################
##	
## author:zhengzebiao		
## date:2019-11-06
##
## 定期删除指定路径下的过期文件
####################################################
delete_dirs=("/home" "/usr/local/elasticsearch/logs/")

for str in ${delete_dirs[@]}
do
find $str -name '*.log*' -mtime +7 -exec rm  {} \;;
#echo $str
done
