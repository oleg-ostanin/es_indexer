package com.example.indexer.parser;


import com.example.indexer.data.Channel;
import com.example.indexer.data.Item;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    private static final String NILS_TEST_INDEX = "nils_test_index";
    private static final String TITLE = "title";

    @Override
    public void run(String... args) throws Exception {
        Channel channel = new RSSParser().parse();

        pushItems(channel.getItems());
    }

    private void pushItems(List<Item> items) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));

        for (int i = 0; i < items.size(); i++) {
            IndexRequest request = createRequest(items.get(i), i);

            IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        }

        client.close();
    }

    private IndexRequest createRequest(Item item, int id) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();

        builder.startObject();
        {
            builder.field(TITLE, item.getTitle());
        }
        builder.endObject();

        IndexRequest indexRequest = new IndexRequest(NILS_TEST_INDEX)
                .id(String.valueOf(id)).source(builder);

        return indexRequest;
    }
}