package org.fugazi.data.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Data model representing a Product.
 */
@Setter @Getter public class Product {

    // Getters and Setters
    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String imageUrl;
    private int quantity;
    private boolean inStock;
    private String sku;

    public Product() {
    }

    public Product(String id, String name, String description, double price, String category,
            String imageUrl, int quantity, boolean inStock, String sku) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.inStock = inStock;
        this.sku = sku;
    }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    public static class ProductBuilder {
        private String id;
        private String name;
        private String description;
        private double price;
        private String category;
        private String imageUrl;
        private int quantity;
        private boolean inStock;
        private String sku;

        public ProductBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder price(double price) {
            this.price = price;
            return this;
        }

        public ProductBuilder category(String category) {
            this.category = category;
            return this;
        }

        public ProductBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public ProductBuilder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public ProductBuilder inStock(boolean inStock) {
            this.inStock = inStock;
            return this;
        }

        public ProductBuilder sku(String sku) {
            this.sku = sku;
            return this;
        }

        public Product build() {
            return new Product(id, name, description, price, category, imageUrl, quantity, inStock, sku);
        }
    }
}

