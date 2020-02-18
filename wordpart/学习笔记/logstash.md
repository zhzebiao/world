

# Logstash

# Logstash初体验

​	Logstash管道有两个不要的元素input和output，和一个可选的元素filter。input插件从数据源消费数据，然后用filter插件将数据按照你的需求定制化，最后使用output插件将数据写到你的存储终端中。

![](F:\world\wordpart\学习笔记\pic\basic_logstash_pipeline.png)

## 1. 控制台输入、输出

为了测试你的Logstash是否安装成功，可以试着运行一下最基础的Logstash的管道

```shell
cd logstash-7.6.0
bin/logstash -e 'input { stdin { } } output { stdout {} }'

# input 
hello world
#output 
{
      "@version" => "1",
    "@timestamp" => 2020-02-18T05:38:29.293Z,
       "message" => "hello world",
          "host" => "localhost.localdomain"
}
```

## 2. 单数据源，多过滤器，单输出端

```yaml
# first-pipeline.conf
input {
    beats {
        port => "5044"
    }
}
filter {
    grok {
        match => { "message" => "%{COMBINEDAPACHELOG}"}	# 将message根据具体格式解析
    }
    geoip {
        source => "clientip"	# 将ipAddress解析详细信息到clientip，
    }
}
output {
    stdout { codec => rubydebug }
}

# 启动filebeat消费日志，输出到logstash
sudo ./filebeat -e -c filebeat.yml -d "publish"
# 启动logstash
bin/logstash -f first-pipeline.conf
```

## 3. 多数据源，多输出端

```yaml
# second-pipeline.conf
input {
    twitter {
        consumer_key => "enter_your_consumer_key_here"
        consumer_secret => "enter_your_secret_here"
        keywords => ["cloud"]
        oauth_token => "enter_your_access_token_here"
        oauth_token_secret => "enter_your_access_token_secret_here"
    }
    beats {
        port => "5044"
    }
}
output {
    elasticsearch {
        hosts => ["IP Address 1:port1", "IP Address 2:port2", "IP Address 3"]
    }
    file {
        path => "/path/to/target/file"
    }
}

# 启动filebeat消费日志，输出到logstash
sudo ./filebeat -e -c filebeat.yml -d "publish"
# 启动logstash
bin/logstash -f second-pipeline.conf
```

# Logstash运行原理

​	Logstash事件处理管理依赖于inputs，filters，outputs三者的协调执行。

​	在Logstash管道中每个input stage都运行在自己的线程之中。Inputs将事件写入到中央队列（内存（默认）或者磁盘）中。每个管道的worker线程从中央队列中获取一批数据，然后将这批数据经过配置好的filters处理，最后输出到outputs中。worker每次获取数据的批量值和worker的线程数可以通过配置修改。

​	在默认情况下，Logstash会在内存中的创建有界队列去缓存pipeline stages之间运算的数据。但假如Logstash意外退出，所有储存在内存中的事件数据都会丢失。为了保护数据不被丢失，可以修改logstash的配置，将游走的数据持久化到磁盘中。



### Logstash配置文件中的数据类型

1. Lists

   ```
   path => [ "/var/log/messages", "/var/log/*.log" ]
   uris => [ "http://elastic.co", "http://example.net" ]
   ```

2. Boolean

   ```
   ssl_enable => true
   ```

3. Bytes

   ```
   my_bytes => "1113"   # 1113 bytes
   my_bytes => "10MiB"  # 10485760 bytes
   my_bytes => "100kib" # 102400 bytes
   my_bytes => "180 mb" # 180000000 bytes
   ```

4. Codec

   ```
   codec => "json"
   ```

5. Hash

   ```
   match => {
     "field1" => "value1"
     "field2" => "value2"
     ...
   }
   # or as a single line. No commas between entries:
   match => { "field1" => "value1" "field2" => "value2" }
   ```

6. Number

   ```
     port => 33
   ```

7. Password

   A password is a string with a single value that is not logged or printed.

   ```
   my_password => "password"
   ```

8. URI

   ```
   my_uri => "http://foo:bar@example.net"
   ```

9. Path

   ```
    my_path => "/tmp/logstash"
   ```

10. String

    ```
      name => "Hello world"
      name => 'It\'s a beautiful day'
    ```

    