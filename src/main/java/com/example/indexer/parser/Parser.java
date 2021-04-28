package com.example.indexer.parser;

import com.example.indexer.data.Item;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

public interface Parser {
    public List<Item> parse(String url) throws ParserConfigurationException, SAXException, IOException;
}
