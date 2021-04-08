package com.example.indexer;

import com.example.indexer.parser.RSStoElasticsearchDataLoader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IndexerApplication implements CommandLineRunner {
    private final RSStoElasticsearchDataLoader dataLoader;

    public IndexerApplication(RSStoElasticsearchDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(IndexerApplication.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        dataLoader.load();

        System.exit(0);
    }
}
