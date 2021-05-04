package com.example.indexer.loader;


import com.example.indexer.data.Item;
import com.example.indexer.loader.ElasticsearchDataLoader;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Loads items to elasticsearch.
 */
@Component
public class ElasticsearchDataLoaderImpl implements ElasticsearchDataLoader {
    private static final String LOCALHOST = "localhost";
    private static final String HTTP = "http";
    private static final int PORT_9200 = 9200;
    private static final int PORT_9201 = 9201;
    private static final String NILS_TEST_INDEX = "nils_test_index";
    private static final String PROPERTIES = "properties";
    private static final String TITLE = "title";
    private static final String TYPE = "type";
    private static final String TEXT = "text";
    private static final String ANALYZER = "analyzer";
    private static final String ENGLISH = "english";
    private static final String LINK = "link";
    private static final String PUB_DATE = "pubDate";
    private static final String COMMENTS = "comments";
    private static final String DESCRIPTION = "description";

    private static final String[] TEXT_FIELDS = new String[]{LINK, PUB_DATE, COMMENTS, DESCRIPTION};

    private RestHighLevelClient client;

    private void setUp() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(LOCALHOST, PORT_9200, HTTP),
                        new HttpHost(LOCALHOST, PORT_9201, HTTP)));
    }

    /**
     * {@inheritDoc}
     */
    public void load(List<Item> items) throws Exception {
        setUp();

        createIndex(NILS_TEST_INDEX);

        pushItems(items);

        tearDown();
    }

    private void createIndex(String name) throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(name);

        // Delete index if exists
        try {
            client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        CreateIndexRequest request = new CreateIndexRequest(NILS_TEST_INDEX);

        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject(PROPERTIES);
            {
                builder.startObject(TITLE);
                {
                    builder.field(TYPE, TEXT);
                    builder.field(ANALYZER, ENGLISH);
                }
                builder.endObject();

                for (String field : TEXT_FIELDS) {
                    builder.startObject(field);
                    {
                        builder.field(TYPE, TEXT);
                    }
                    builder.endObject();
                }
            }
            builder.endObject();
        }
        builder.endObject();
        request.mapping(builder);

        client.indices().create(request, RequestOptions.DEFAULT);
    }

    private void pushItems(List<Item> items) throws IOException {
        for (int i = 0; i < items.size(); i++) {
            IndexRequest request = createRequest(items.get(i), i);

            client.index(request, RequestOptions.DEFAULT);
        }
    }

    private IndexRequest createRequest(Item item, int id) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();

        builder.startObject();

        builder.field(TITLE, item.getTitle());
        builder.field(LINK, item.getLink().toString());
        builder.field(PUB_DATE, item.getPubDate());
        builder.field(COMMENTS, item.getComments());
        builder.field(DESCRIPTION, item.getDescription());

        builder.endObject();

        return new IndexRequest(NILS_TEST_INDEX)
                .id(String.valueOf(id))
                .source(builder);
    }

    private void tearDown() throws IOException {
        client.close();
    }
}