package com.example.indexer.parser;

import com.example.indexer.data.Item;

import java.util.List;

/**
 * Reads data from RSS feed and loads it to elasticsearch.
 */
public interface ElasticsearchDataLoader {
    /**
     * Reads data from RSS feed and loads it to elasticsearch.
     *
     * @throws Exception if failed.
     */
    void load(List<Item> items) throws Exception;
}
