package com.example.indexer.parser;

import com.example.indexer.data.Item;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 * Parses RSS feed.
 */
public interface RSSParser {
    /**
     *
     * @param url Url to get feed from.
     * @return List of items.
     * @throws ParserConfigurationException If failed.
     * @throws SAXException If failed.
     * @throws IOException If failed.
     */
    List<Item> parse(String url) throws ParserConfigurationException, SAXException, IOException;
}
