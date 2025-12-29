package com.example.pet_inventory.models;

import java.math.BigDecimal;

public class ProductDTO {
    private String productUPC;
    private String productName;
    private Category productCategory;
    private BigDecimal productPrice;
    private int productQuantity;
    private int numOfSold;

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
}
