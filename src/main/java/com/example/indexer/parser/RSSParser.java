package com.example.indexer.parser;

import com.example.indexer.data.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RSSParser {
    private static final String RSS_URL_STR = "https://news.ycombinator.com/rss";

    private Channel currentChannel;
    private Element currentElement;
    private Item currentItem;

    private String currentText;

    public Channel parse() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        URL rssUrl = new URL(RSS_URL_STR);

        XMLHandler handler = new XMLHandler();
        parser.parse(rssUrl.openStream(), handler);

        return currentChannel;
    }

    private class XMLHandler extends DefaultHandler {
        private final DateFormat format = new SimpleDateFormat("E, dd MMM YYYY HH:MM:SS +ZZZZ", Locale.ENGLISH);

        @Override
        public void startDocument() throws SAXException {
            // Тут будет логика реакции на начало документа
        }

        @Override
        public void endDocument() throws SAXException {
            // Тут будет логика реакции на конец документа
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            IndexerTag tag = IndexerTag.valueOf(qName.toUpperCase());

            switch (tag) {
                case RSS: {
                    //NO_OP
                }
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
                case RSS: {
                    //NO_OP
                }
                case CHANNEL: {
                    //NO_OP
                }
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

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            // Тут будет логика реакции на пустое пространство внутри элементов (пробелы, переносы строчек и так далее).
        }
    }
}
