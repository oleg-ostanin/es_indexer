package com.example.indexer.parser;

/**
 * Reads data from RSS feed and loads it to elasticsearch.
 */
public interface RSStoElasticsearchDataLoader {
    /**
     * Reads data from RSS feed and loads it to elasticsearch.
     *
     * @throws Exception if failed.
     */
    void load() throws Exception;
}
