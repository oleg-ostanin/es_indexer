package com.example.indexer.loader;

import com.example.indexer.data.Item;

import java.util.List;

/**
 * Loads items to elasticsearch.
 */
public interface ElasticsearchDataLoader {
    /**
     * Loads items to elasticsearch.
     *
     * @throws Exception if failed.
     */
    void load(List<Item> items) throws Exception;
}
