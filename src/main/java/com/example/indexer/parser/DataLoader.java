package com.example.indexer.parser;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    public DataLoader() {

    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Loader");

        new SAXExample().parse(args);
    }
}