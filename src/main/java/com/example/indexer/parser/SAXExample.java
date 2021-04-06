package com.example.indexer.parser;

import com.example.indexer.data.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SAXExample {
    private static final String path = "rss.xml";
    
    private Channel currentChannel;
    private Element currentElement;
    private Item currentItem;

    private String currentText;

    public void parse(String[] args) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        File rss = new File(SAXExample.class.getClassLoader().getResource(path).getFile());


        XMLHandler handler = new XMLHandler();
        parser.parse(rss, handler);

        System.out.println();
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

            System.out.println();
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

                }
                case LINK: {
                    try {
                        currentElement.setLink(new URI(currentText));

                        System.out.println();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case TITLE: {
                    currentElement.setTitle(new Title(currentText));
                }
                case DESCRIPTION: {
                    currentElement.setDescription(new Description(currentText));
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

            System.out.println();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            currentText = new String(ch).substring(start, start + length);

            System.out.println();
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            // Тут будет логика реакции на пустое пространство внутри элементов (пробелы, переносы строчек и так далее).
        }
    }
}
