package org.example;

import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.example.model.Orders;
import org.example.model.Products;
import org.example.model.Shipments;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class AppTest {
    @Test
    public void test() throws IOException {
        RestHighLevelClient restHighLevelClient =
                ElasticsearchClient.createRestHighLevelClient("http://127.0.0.1:9200",
                        "elastic", "elasticpw");

        SearchRequest searchRequest = Requests.searchRequest("orders");

        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource()
                .query(QueryBuilders.matchAllQuery()).trackTotalHits(true);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();

        List<Orders> ordersList = new ArrayList<>();
        for (SearchHit hit : hits.getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Orders orders = ObjectMapperUtils.convertValue(sourceAsMap, Orders.class);
            ordersList.add(orders);
        }

        for (Orders orders : ordersList) {
            System.out.println(ObjectMapperUtils.writeValueAsString(orders));
        }

        searchRequest = Requests.searchRequest("products");

        searchSourceBuilder = SearchSourceBuilder.searchSource()
                .query(QueryBuilders.matchAllQuery()).trackTotalHits(true);
        searchRequest.source(searchSourceBuilder);
        searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        hits = searchResponse.getHits();

        List<Products> productsList = new ArrayList<>();
        for (SearchHit hit : hits.getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Products products = ObjectMapperUtils.convertValue(sourceAsMap, Products.class);
            productsList.add(products);
        }

        for (Products products : productsList) {
            System.out.println(ObjectMapperUtils.writeValueAsString(products));
        }

        searchRequest = Requests.searchRequest("shipments");

        searchSourceBuilder = SearchSourceBuilder.searchSource()
                .query(QueryBuilders.matchAllQuery()).trackTotalHits(true);
        searchRequest.source(searchSourceBuilder);
        searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        hits = searchResponse.getHits();

        List<Shipments> shipmentsList = new ArrayList<>();
        for (SearchHit hit : hits.getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Shipments shipments = ObjectMapperUtils.convertValue(sourceAsMap, Shipments.class);
            shipmentsList.add(shipments);
        }

        for (Shipments shipments : shipmentsList) {
            System.out.println(ObjectMapperUtils.writeValueAsString(shipments));
        }
    }

    @Test
    public void test2() throws JsonProcessingException {
        Date date = new Date(1682941811000L);
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(date);
        System.out.println(ObjectMapperUtils.writeValueAsString(date));
        System.out.println(localDateTime);
        System.out.println(ObjectMapperUtils.writeValueAsString(localDateTime));

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        String dateStr = "2023-05-01 19:50:11";
        date = ObjectMapperUtils.convertValue(dateStr, Date.class);
        localDateTime = ObjectMapperUtils.convertValue(dateStr, LocalDateTime.class);
        System.out.println(date);
        System.out.println(localDateTime);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        String dateJson = "{\"date\":\"2023-05-01 19:50:11\"}";
        String localDateTimeJson = "{\"localDateTime\":\"2023-05-01 19:50:11\"}";
        System.out.println(ObjectMapperUtils.readValue(dateJson, new TypeReference<Map<String, Date>>() {
        }));
        System.out.println(ObjectMapperUtils.readValue(localDateTimeJson, new TypeReference<Map<String, LocalDateTime>>() {
        }));
    }
}
