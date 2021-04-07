package com.example.indexer.data;

import lombok.Data;


@Data
public class Item extends Element {
    //todo change to Date
    private String pubDate;
    private String comments;
    private String[] tags;
}
