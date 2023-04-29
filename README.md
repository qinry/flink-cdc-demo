## flink-cdc-demo

这是使用 Flink CDC 同步 MySQL 数据到 Elasticsearch的演示

### 版本

mysql 5.6.51

elasticsearch 7.10.2

最低 java 8,到java11

maven 3.6.3

### 搭建演示环境

使用docker compose启动环境

```bash
cd docker
docker compose up -d
```

进入mysql（密码在docker-compose.yaml,自行修改），给用户赋权

```
grant all privileges on *.* to 'mysqluser'@'%';
flush privileges;
```

### 启动Flink CDC程序

```bash
cd ../
mvn clean install -DskipTests
java -jar target/flink-cdc-demo-0.1.0.jar 
```

### 登录Kibana验证数据

浏览器访问 http://localhost:5601（密码在docker-compose.yaml，自行修改）, 进入 Dev Tools

查询数据
```
POST products/_search
{
    "query": {
        "match_all": {}
    }
}
```

有数据则说明同步ES成功

### 清理

```
mvn clean
cd docker
docker compose down
```

### 注意

改了MySQL或Elasticsearch密码，FlinkCDC.java相应也要修改