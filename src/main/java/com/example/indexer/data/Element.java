package com.example.indexer.data;

import lombok.Data;

import java.net.URI;

@Data
public class Element {
    private String title;
    private URI link;
    private String description;
}
