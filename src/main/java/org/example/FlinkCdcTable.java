package org.example;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * Table API 实现MySql多表连接同步到ES
 */
public class FlinkCdcTable {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 每隔3s做一次checkpoint，方便测试，在生产使用5到10min
        env.enableCheckpointing(3000L);
        env.setParallelism(1);
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);
        // 在flink内存创建源表orders、products、shipments连接的是MySql外部表
        tEnv.executeSql("CREATE TABLE orders (\n" +
                "   order_id INT,\n" +
                "   order_date TIMESTAMP(0),\n" +
                "   customer_name STRING,\n" +
                "   price DECIMAL(10, 5),\n" +
                "   product_id INT,\n" +
                "   order_status BOOLEAN,\n" +
                "   PRIMARY KEY (order_id) NOT ENFORCED\n" +
                " ) WITH (\n" +
                "   'connector' = 'mysql-cdc',\n" +
                "   'hostname' = 'localhost',\n" +
                "   'port' = '3306',\n" +
                "   'username' = 'mysqluser',\n" +
                "   'password' = 'mysqlpw',\n" +
                "   'database-name' = 'mydb',\n" +
                "   'table-name' = 'orders'\n" +
                " )\n");

        tEnv.executeSql("CREATE TABLE products (\n" +
                "    id INT,\n" +
                "    name STRING,\n" +
                "    description STRING,\n" +
                "    PRIMARY KEY (id) NOT ENFORCED\n" +
                "  ) WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'hostname' = 'localhost',\n" +
                "    'port' = '3306',\n" +
                "    'username' = 'mysqluser',\n" +
                "    'password' = 'mysqlpw',\n" +
                "    'database-name' = 'mydb',\n" +
                "    'table-name' = 'products'\n" +
                "  )");

        tEnv.executeSql("CREATE TABLE shipments (\n" +
                "   shipment_id INT,\n" +
                "   order_id INT,\n" +
                "   origin STRING,\n" +
                "   destination STRING,\n" +
                "   is_arrived BOOLEAN,\n" +
                "   PRIMARY KEY (shipment_id) NOT ENFORCED\n" +
                " ) WITH (\n" +
                "   'connector' = 'mysql-cdc',\n" +
                "   'hostname' = 'localhost',\n" +
                "   'port' = '3306',\n" +
                "   'username' = 'mysqluser',\n" +
                "   'password' = 'mysqlpw',\n" +
                "   'database-name' = 'mydb',\n" +
                "   'table-name' = 'shipments'\n" +
                " )");
        // 在flink内存创建目标表enriched_orders连接的是ES外部表
        tEnv.executeSql("CREATE TABLE enriched_orders (\n" +
                "   order_id INT,\n" +
                "   order_date TIMESTAMP(0),\n" +
                "   customer_name STRING,\n" +
                "   price DECIMAL(10, 5),\n" +
                "   product_id INT,\n" +
                "   order_status BOOLEAN,\n" +
                "   product_name STRING,\n" +
                "   product_description STRING,\n" +
                "   shipment_id INT,\n" +
                "   origin STRING,\n" +
                "   destination STRING,\n" +
                "   is_arrived BOOLEAN,\n" +
                "   PRIMARY KEY (order_id) NOT ENFORCED\n" +
                " ) WITH (\n" +
                "     'connector' = 'elasticsearch-7',\n" +
                "     'hosts' = 'http://localhost:9200',\n" +
                "     'index' = 'enriched_orders',\n" +
                "     'username' = 'elastic',\n" +
                "     'password' = 'elasticpw'\n" +
                " )");
        // 连接查询源表后插入目标表
        tEnv.executeSql("INSERT INTO enriched_orders\n" +
                " SELECT o.*, p.name, p.description, s.shipment_id, s.origin, s.destination, s.is_arrived\n" +
                " FROM orders AS o\n" +
                " LEFT JOIN products AS p ON o.product_id = p.id\n" +
                " LEFT JOIN shipments AS s ON o.order_id = s.order_id");

        env.execute("MySql To Elasticsearch");
    }
}
