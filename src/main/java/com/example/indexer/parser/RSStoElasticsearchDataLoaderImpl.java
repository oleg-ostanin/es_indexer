package com.example.indexer.parser;


import com.example.indexer.data.Channel;
import com.example.indexer.data.Item;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads data from RSS feed and loads it to elasticsearch.
 */
@Component
public class RSStoElasticsearchDataLoaderImpl implements RSStoElasticsearchDataLoader {
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
    private static final String TITLE_TAGS = "title_tags";
    private static final String LINK = "link";
    private static final String PUB_DATE = "pubDate";
    private static final String COMMENTS = "comments";
    private static final String DESCRIPTION = "description";

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
    public void load() throws Exception {
        setUp();

        Channel channel = new RSSParser().parse();

        createIndex();

        pushItems(channel.getItems());

        tearDown();
    }

    private void createIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(NILS_TEST_INDEX);

        try {
            AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        CreateIndexRequest request = new CreateIndexRequest(NILS_TEST_INDEX);

        Map<String, String> titleMap = new HashMap<>();
        titleMap.put(TYPE, TEXT);
        titleMap.put(ANALYZER, ENGLISH);

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
                builder.startObject(LINK);
                {
                    builder.field(TYPE, TEXT);
                }
                builder.endObject();
                builder.startObject(PUB_DATE);
                {
                    builder.field(TYPE, TEXT);
                }
                builder.endObject();
                builder.startObject(COMMENTS);
                {
                    builder.field(TYPE, TEXT);
                }
                builder.endObject();
                builder.startObject(DESCRIPTION);
                {
                    builder.field(TYPE, TEXT);
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        request.mapping(builder);



        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

        System.out.println(createIndexResponse);
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
        //builder.array(TITLE_TAGS, getTags(item.getTitle()));
        builder.field(LINK, item.getLink().toString());
        builder.field(PUB_DATE, item.getPubDate());
        builder.field(COMMENTS, item.getComments());
        builder.field(DESCRIPTION, item.getDescription());

        builder.endObject();

        return new IndexRequest(NILS_TEST_INDEX)
                .id(String.valueOf(id))
                .source(builder);
    }

    /**
     * Converts each word in a title to more general form to perform better search.
     *
     * @param title Initial title.
     * @return Tags array.
     * @throws IOException if failed.
     */
    private String[] getTags(String title) throws IOException {
        AnalyzeRequest analyzeRequest = AnalyzeRequest.withGlobalAnalyzer("english", title);

        AnalyzeResponse response = client.indices().analyze(analyzeRequest, RequestOptions.DEFAULT);

        return response.getTokens().stream()
                .map(AnalyzeResponse.AnalyzeToken::getTerm)
                .filter(term -> term.length() > 1)
                .distinct()
                .toArray(String[]::new);
    }

    private void tearDown() throws IOException {
        client.close();
    }
}