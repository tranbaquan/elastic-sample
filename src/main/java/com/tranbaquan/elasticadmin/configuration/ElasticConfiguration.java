package com.tranbaquan.elasticadmin.configuration;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfiguration {

    @Value("${elastic.client.hostname}")
    private String hostname;

    @Value("${elastic.client.port}")
    private int port;

    @Value("${elastic.client.schema}")
    private String schema;

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port, schema)));
    }
}
