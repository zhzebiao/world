#!/bin/bash
####################################################
##	
## author:zhengzebiao		
## date:2019-11-06
##
## 定期删除HBase中的过期数据
####################################################

base_dir=$(cd `dirname $0`;  pwd)
phoenix_dir="/data/"
zk_conn="44.152.1.84:2181"

START_TIME=`date -d "7 months ago" +%Y-%m-%d`
END_TIME=`date -d "6 months ago" +%Y-%m-%d`
DELETE_SQL="delete from c_picrecord where cap_date >='START_TIME 00:00:00' and cap_date<'END_TIME 00:00:00';"

# 替换sql中的变量
DELETE_SQL=${DELETE_SQL/START_TIME/"$START_TIME"}
DELETE_SQL=${DELETE_SQL/END_TIME/"$END_TIME"}
echo ${DELETE_SQL} > delete.sql

nohup $phoenix_dir/bin/sqlline.py $zk_conn $base_dir/delete.sql >> nohup.out
