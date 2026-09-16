package com.example.chatbot.service;

import com.example.chatbot.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

@Service
public class ProductSearchService {

    private final EmbeddingService embeddingService;
    private List<Product> products;

    public ProductSearchService(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    // Load the product catalog once at startup and pre-compute an embedding for each item.
    // Fine for a small demo catalog; for a real catalog you'd do this offline and store
    // the vectors in a database instead of recomputing them every boot.
    @PostConstruct
    public void init() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = new ClassPathResource("products.json").getInputStream()) {
            products = mapper.readValue(in, mapper.getTypeFactory()
                    .constructCollectionType(List.class, Product.class));
        }

        for (Product product : products) {
            product.setEmbedding(embeddingService.embed(product.toContextText()));
        }
    }

    public List<Product> search(String query, int topK) {
        List<Double> queryEmbedding = embeddingService.embed(query);

        return products.stream()
                .sorted(Comparator.comparingDouble(
                        (Product p) -> cosineSimilarity(queryEmbedding, p.getEmbedding())).reversed())
                .limit(topK)
                .toList();
    }

    private double cosineSimilarity(List<Double> a, List<Double> b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.size(); i++) {
            dot += a.get(i) * b.get(i);
            normA += Math.pow(a.get(i), 2);
            normB += Math.pow(b.get(i), 2);
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
