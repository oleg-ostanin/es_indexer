package com.example.indexer.data;

import lombok.Data;

import java.util.Date;

@Data
public class Item extends Element {
    //todo change to Date
    private String pubDate;
    private String comments;
    private String[] tags;
}
