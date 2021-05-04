package com.example.indexer;

import com.example.indexer.loader.ElasticsearchDataLoader;
import com.example.indexer.parser.RSSParser;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@SpringBootApplication
public class IndexerApplication implements CommandLineRunner {
    private static final String RSS_URL_STR = "https://news.ycombinator.com/rss";

    private final ElasticsearchDataLoader dataLoader;
    private final RSSParser parser;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(IndexerApplication.class);
        app.run(args).close();
    }

    @Override
    public void run(String... args) throws Exception {
        dataLoader.load(parser.parse(RSS_URL_STR));
    }
}
