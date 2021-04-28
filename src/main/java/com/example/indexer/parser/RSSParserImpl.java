package com.example.indexer.parser;

import com.example.indexer.data.Channel;
import com.example.indexer.data.Element;
import com.example.indexer.data.IndexerTag;
import com.example.indexer.data.Item;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Service
public class RSSParserImpl implements Parser {
    private Channel currentChannel;
    private Element currentElement;
    private Item currentItem;

    private String currentText;

    public List<Item> parse(String url) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        URL rssUrl = new URL(url);

        XMLHandler handler = new XMLHandler();

        InputStream stream = rssUrl.openStream();

        parser.parse(stream, handler);

        stream.close();

        return currentChannel.getItems();
    }

    private class XMLHandler extends DefaultHandler {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            IndexerTag tag = IndexerTag.valueOf(qName.toUpperCase());

            switch (tag) {
                case CHANNEL: {
                    currentChannel = new Channel();
                    currentElement = currentChannel;
                    break;
                }
                case ITEM: {
                    currentItem = new Item();
                    currentElement = currentItem;
                    break;
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            IndexerTag tag = IndexerTag.valueOf(qName.toUpperCase());

            switch (tag) {
                case ITEM: {
                    currentElement = currentChannel;
                    currentChannel.getItems().add(currentItem);
                    break;
                }
                case LINK: {
                    try {
                        currentElement.setLink(new URI(currentText));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case TITLE: {
                    currentElement.setTitle(currentText);
                }
                case DESCRIPTION: {
                    currentElement.setDescription(currentText);
                    break;
                }
                case PUBDATE: {
                    currentItem.setPubDate(currentText);
                    break;
                }
                case COMMENTS: {
                    currentItem.setComments(currentText);
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            currentText = new String(ch).substring(start, start + length);
        }
    }
}
