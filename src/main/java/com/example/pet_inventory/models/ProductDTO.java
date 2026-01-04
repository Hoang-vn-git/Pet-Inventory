package com.example.pet_inventory.models;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for Product.
 * Used for API responses or JSON parsing where JavaFX properties are not needed.
 */
public class ProductDTO {

    private String productUPC;        // Unique product code
    private String productName;       // Product name
    private Category productCategory; // Product category
    private BigDecimal productPrice;  // Unit price
    private int productQuantity;      // Available quantity
    private int numOfSold;            // Number of sold items

    // ---------------- Getters & Setters ----------------

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

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public int getNumOfSold() {
        return numOfSold;
    }

    public void setNumOfSold(int numOfSold) {
        this.numOfSold = numOfSold;
    }

    // Optional: nice string representation for debugging
    @Override
    public String toString() {
        return "ProductDTO{" +
                "UPC='" + productUPC + '\'' +
                ", Name='" + productName + '\'' +
                ", Category=" + productCategory +
                ", Price=" + productPrice +
                ", Quantity=" + productQuantity +
                ", Sold=" + numOfSold +
                '}';
    }
}