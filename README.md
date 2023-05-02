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

进入kibana,浏览器访问 http://localhost:5601 （密码在docker-compose.yaml，自行修改）, 进入 Dev Tools

创建索引的mapping(这里示例一个索引，更多见es/mapping.txt)

```
PUT enriched_orders
{
  "mappings": {
    "properties": {
      "customer_name": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "destination": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "is_arrived": {
        "type": "boolean"
      },
      "order_date": {
        "type": "date",
        "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
      },
      "order_id": {
        "type": "long"
      },
      "order_status": {
        "type": "boolean"
      },
      "origin": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "price": {
        "type": "float"
      },
      "product_description": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "product_id": {
        "type": "long"
      },
      "product_name": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "shipment_id": {
        "type": "long"
      }
    }
  }
}
```

### 启动Flink CDC程序

```bash
cd ../
mvn clean install -DskipTests
java -jar target/flink-cdc-demo-0.1.0.jar 
```

### 验证数据

在kibana查询数据

```
POST enriched_orders/_search
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

- 改了MySQL或Elasticsearch密码，FlinkCDC.java相应也要修改
- 这里演示的是使用Table API实现MySql多表连接同步到ES，在pom.xml把mainClass配置修改为FlinkCdcDs，
再`mvn clean install -DskipTests`后，接着启动程序，将演示的是使用DataStream API实现MySql单表同步ES
