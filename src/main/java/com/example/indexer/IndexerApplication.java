package com.example.indexer;

import com.example.indexer.parser.Parser;
import com.example.indexer.parser.ElasticsearchDataLoader;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@AllArgsConstructor
@SpringBootApplication
public class IndexerApplication implements CommandLineRunner {
    private static final String RSS_URL_STR = "https://news.ycombinator.com/rss";

    private final ElasticsearchDataLoader dataLoader;
    private final Parser parser;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(IndexerApplication.class);
        app.run(args).close();
    }

    @Override
    public void run(String... args) throws Exception {
        dataLoader.load(parser.parse(RSS_URL_STR));
    }
}
