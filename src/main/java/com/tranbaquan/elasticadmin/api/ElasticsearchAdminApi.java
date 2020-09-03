package com.tranbaquan.elasticadmin.api;

import com.tranbaquan.elasticadmin.model.ChartData;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetFieldMappingsRequest;
import org.elasticsearch.client.indices.GetFieldMappingsResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class ElasticsearchAdminApi {

    private RestHighLevelClient elasticsearchClient;

    @Value("${elastic.index.name}")
    private String indexName;

    @Autowired
    public void setElasticsearchClient(RestHighLevelClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    @CrossOrigin(origins = "${crossOrigin.origin}")
    @GetMapping(value = "/fields")
    public Mono<Set<String>> getAllFields() {
        GetFieldMappingsRequest request = new GetFieldMappingsRequest().indices(indexName).fields("*");

        return Mono.create(sink -> elasticsearchClient.indices().getFieldMappingAsync(request, RequestOptions.DEFAULT, new ActionListener<GetFieldMappingsResponse>() {
            @Override
            public void onResponse(GetFieldMappingsResponse response) {
                sink.success(response.mappings().get(indexName).keySet().stream()
                        .filter(field -> !field.startsWith("_")).collect(Collectors.toSet()));
            }

            @Override
            public void onFailure(Exception e) {
                sink.error(e);
            }
        }));
    }

    @CrossOrigin(origins = "${crossOrigin.origin}")
    @GetMapping("/chart-data")
    public Mono<ChartData> getBarCharts(@RequestParam String fieldName) {
        AggregationBuilder aggregations = AggregationBuilders.terms("agg").field(fieldName);
        SearchSourceBuilder builder = new SearchSourceBuilder().size(0).aggregation(aggregations);
        SearchRequest request = new SearchRequest().indices(indexName).source(builder);

        return Mono.create(sink -> elasticsearchClient.searchAsync(request, RequestOptions.DEFAULT, new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse searchResponse) {
                Terms agg = searchResponse.getAggregations().get("agg");
                ChartData chart = new ChartData();
                for (Terms.Bucket bucket : agg.getBuckets()) {
                    chart.getLabels().add(bucket.getKey().toString());
                    chart.getData().add(bucket.getDocCount());
                }
                sink.success(chart);
            }

            @Override
            public void onFailure(Exception e) {
                sink.error(e);
            }
        }));
    }

    @CrossOrigin(origins = "${crossOrigin.origin}")
    @GetMapping("chart-data/order")
    public Mono<ChartData> getOrdersLastDay() {
        AggregationBuilder aggregations = AggregationBuilders.dateHistogram("orders_last_day").field("order_date")
                .calendarInterval(DateHistogramInterval.HOUR);
        SearchSourceBuilder builder = new SearchSourceBuilder().size(0).aggregation(aggregations)
                .query(QueryBuilders.rangeQuery("order_date").gte("now-1d/d").lte("now/d"));
        SearchRequest request = new SearchRequest().indices(indexName).source(builder);

        return Mono.create(sink -> elasticsearchClient.searchAsync(request, RequestOptions.DEFAULT, new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse searchResponse) {
                ParsedDateHistogram agg = searchResponse.getAggregations().get("orders_last_day");
                ChartData chart = new ChartData();
                String label;
                for (Histogram.Bucket bucket : agg.getBuckets()) {
                    label = bucket.getKeyAsString().substring(0, bucket.getKeyAsString().length() - 5);
                    chart.getLabels().add(label);
                    chart.getData().add(bucket.getDocCount());
                }

                sink.success(chart);
            }

            @Override
            public void onFailure(Exception e) {
                sink.error(e);
            }
        }));
    }
}
