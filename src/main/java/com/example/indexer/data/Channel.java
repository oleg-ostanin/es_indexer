package com.example.indexer.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Channel extends Element{
    private final List<Item> items = new ArrayList<>();
}
