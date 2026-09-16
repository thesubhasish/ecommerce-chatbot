package com.example.chatbot.model;

import java.util.List;

public class Product {

    private String id;
    private String name;
    private String description;
    private double price;

    // Filled in at startup by EmbeddingService — not part of the JSON input file
    private List<Double> embedding;

    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Double> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Double> embedding) {
        this.embedding = embedding;
    }

    // Text that gets embedded and shown to the model as context
    public String toContextText() {
        return name + " - " + description + " (Price: $" + price + ")";
    }
}
