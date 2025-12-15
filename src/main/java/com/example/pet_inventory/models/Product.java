package com.example.pet_inventory.models;

import java.math.BigDecimal;

public class Product {
    private String productUPC;
    private String productName;
    private Category productCategory;
    private int quantity;
    private BigDecimal price;


    public Product(String productUPC, String productName, Category productCategory, int quantity, BigDecimal price) {
        this.productUPC = productUPC;
        this.productName = productName;
        this.productCategory = productCategory;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductUPC() {
        return productUPC;
    }

    public void setProductUPC(String productUPC) {
        this.productUPC = productUPC;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Category getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(Category productCategory) {
        this.productCategory = productCategory;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    @Override
    public String toString() {
        return "Product Details:\n" +
                "  UPC: " + productUPC + "\n" +
                "  Name: " + productName + "\n" +
                "  Category: " + productCategory + "\n" +
                "  Quantity: " + quantity + "\n" +
                "  Price: $" + price + "\n" + "-------------------------------------" + "\n";
    }
}
