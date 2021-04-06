package com.example.indexer.data;

import lombok.Data;

import java.net.URI;

@Data
public class Element {
    private Title title;
    private URI link;
    private Description description;
}
