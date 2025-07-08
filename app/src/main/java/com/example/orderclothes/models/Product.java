package com.example.orderclothes.models;

public class Product {
    private int productId;
    private String productName;
    private String description;
    private double price;
    private int categoryId;
    private String brand;
    private String material;
    private String imageUrl;
    private int stockQuantity;
    private boolean isActive;
    private String createdAt;
    private String updatedAt;

    // Constructors
    public Product() {}

    public Product(int productId, String productName, String description, double price,
                   int categoryId, String brand, String material, String imageUrl,
                   int stockQuantity, boolean isActive, String createdAt, String updatedAt) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.brand = brand;
        this.material = material;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", categoryId=" + categoryId +
                ", brand='" + brand + '\'' +
                ", material='" + material + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", isActive=" + isActive +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
