package org.example;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Arrays;
import java.util.List;

public class ElasticsearchClient {
    public static RestHighLevelClient createRestHighLevelClient(List<String> uris, String username, String password) {
        HttpHost[] httpHosts = uris.stream().map(HttpHost::create)
                .toArray(HttpHost[]::new);
        return getRestHighLevelClient(username, password, httpHosts);
    }

    public static RestHighLevelClient createRestHighLevelClient(String uris, String username, String password) {
        HttpHost[] httpHosts = Arrays.stream(uris.split(",")).map(HttpHost::create)
                .toArray(HttpHost[]::new);
        return getRestHighLevelClient(username, password, httpHosts);
    }

    private static RestHighLevelClient getRestHighLevelClient(String username, String password, HttpHost[] httpHosts) {
        RestClientBuilder restClientBuilder = RestClient.builder(httpHosts);
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(60000)
                .build();
        restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder ->
                httpAsyncClientBuilder.setDefaultRequestConfig(requestConfig)
                        .setDefaultCredentialsProvider(credentialsProvider));
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(restClientBuilder);
        return restHighLevelClient;
    }
}
