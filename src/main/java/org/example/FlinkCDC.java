package org.example;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;
import org.apache.flink.streaming.connectors.elasticsearch7.RestClientFactory;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestClientBuilder;
import org.example.model.Orders;
import org.example.model.Products;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FlinkCDC {
    private static final Logger log = LoggerFactory.getLogger(FlinkCDC.class);

    public static void main(String[] args) throws Exception {
        Properties debeziumProperties = new Properties();

        debeziumProperties.setProperty("bigint.unsigned.handling.mode","long");
        debeziumProperties.setProperty("decimal.handling.mode","double");

        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("127.0.0.1")
                .port(3306)
                .databaseList("mydb")
                .tableList("mydb.products,mydb.orders")
                .username("mysqluser")
                .password("mysqlpw")
                .serverTimeZone("Asia/Shanghai")
                .debeziumProperties(debeziumProperties)
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置 3s 的 checkpoint 间隔
        env.enableCheckpointing(3000);

        DataStream<String> input = env
                .fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source");
        env.setParallelism(4);
        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost("127.0.0.1", 9200, "http"));

        TableIndexConvertor.put("products", "products");
        TableIndexConvertor.put("orders", "orders");

        GetSourceMapExecutor.putGetIdFunction("products", element -> DebeziumJsonUtils.getDataPrimaryKey(element, "id"));
        GetSourceMapExecutor.putGetIdFunction("orders", element -> DebeziumJsonUtils.getDataPrimaryKey(element, "order_id"));

        GetSourceMapExecutor.putGetSourceMapFunction("products", element -> DebeziumJsonUtils.convertEntity(element, Products.class));
        GetSourceMapExecutor.putGetSourceMapFunction("orders", element -> DebeziumJsonUtils.convertEntity(element, Orders.class));

        ElasticsearchSink.Builder<String> esSinkBuilder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<String>() {
                    private IndexRequest createIndexRequest(String element) {
                        String op = DebeziumJsonUtils.getOp(element);
                        String table = DebeziumJsonUtils.getTable(element);
                        String indexName = TableIndexConvertor.fromTable(table);
                        String afterString = DebeziumJsonUtils.getAfterString(element);
                        Object source = GetSourceMapExecutor.getSource(table, afterString);
                        Map<String, ?> sourceMap = DebeziumJsonUtils.convertMap(source);
                        String id = GetSourceMapExecutor.getIdValue(table, afterString);
                        if (StringUtils.equals("c", op)) {
                            return Requests.indexRequest().index(indexName).id(id)
                                    .source(sourceMap)
                                    .opType(DocWriteRequest.OpType.CREATE);
                        } else if (StringUtils.equals("r", op)) {
                            return Requests.indexRequest().index(indexName).id(id)
                                    .source(sourceMap)
                                    .opType(DocWriteRequest.OpType.INDEX);
                        } else if (StringUtils.equals("u", op)) {
                            return Requests.indexRequest().index(indexName).id(id)
                                    .source(sourceMap)
                                    .opType(DocWriteRequest.OpType.INDEX);
                        }
                        return null;
                    }

                    @Override
                    public void process(String element, RuntimeContext ctx, RequestIndexer indexer) {
                        log.info(">>>{}", element);
                        String op = DebeziumJsonUtils.getOp(element);
                        if (StringUtils.equals("d", op)) {
                            String table = DebeziumJsonUtils.getTable(element);
                            String indexName = TableIndexConvertor.fromTable(table);
                            String beforeString = DebeziumJsonUtils.getBeforeString(element);
                            String id = GetSourceMapExecutor.getIdValue(table, beforeString);
                            indexer.add(Requests.deleteRequest(indexName)
                                    .id(id));
                        } else {
                            Optional.ofNullable(createIndexRequest(element)).ifPresent(indexer::add);
                        }
                    }
                }
        );

        esSinkBuilder.setBulkFlushMaxActions(1);

        esSinkBuilder.setRestClientFactory(getRestClientFactory());

        input.addSink(esSinkBuilder.build()).name("Elasticsearch Sink").setParallelism(1);
        env.execute("Sync MySQL to Elasticsearch");
    }

    private static RestClientFactory getRestClientFactory() {
        return restClientBuilder -> restClientBuilder.setHttpClientConfigCallback(getHttpClientConfigCallback());
    }

    private static RestClientBuilder.HttpClientConfigCallback getHttpClientConfigCallback() {
        BasicCredentialsProvider credentialsProvider
                = getCustomBasicCredentialsProvider("elastic", "elasticpw");
        RequestConfig requestConfig = getCustomRequestConfig();
        return httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultRequestConfig(requestConfig)
                .setDefaultCredentialsProvider(credentialsProvider);
    }

    private static BasicCredentialsProvider getCustomBasicCredentialsProvider(String username, String password) {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        return credentialsProvider;
    }

    private static RequestConfig getCustomRequestConfig() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(60000)
                .build();
        return requestConfig;
    }
}
