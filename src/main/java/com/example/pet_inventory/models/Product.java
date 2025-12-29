package com.example.pet_inventory.models;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.math.BigDecimal;

public class Product {

    private final StringProperty productUPC;
    private final StringProperty productName;
    private final ObjectProperty<Category> productCategory;
    private final IntegerProperty quantity;
    private final ObjectProperty<BigDecimal> price;
    private final IntegerProperty numOfSoldProduct;

    public Product(String productUPC, String productName, Category productCategory, int quantity, BigDecimal price, int numOfSoldProduct) {
        this.productUPC = new SimpleStringProperty(productUPC);
        this.productName = new SimpleStringProperty(productName);
        this.productCategory = new SimpleObjectProperty<>(productCategory);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleObjectProperty<>(price);
        this.numOfSoldProduct = new SimpleIntegerProperty(numOfSoldProduct);;
    }

    // ===================== Property getters =====================
    public StringProperty productUPCProperty() { return productUPC; }
    public StringProperty productNameProperty() { return productName; }
    public ObjectProperty<Category> productCategoryProperty() { return productCategory; }
    public IntegerProperty quantityProperty() { return quantity; }
    public ObjectProperty<BigDecimal> priceProperty() { return price; }

    // ===================== Standard getters/setters =====================
    public String getProductUPC() { return productUPC.get(); }
    public void setProductUPC(String productUPC) { this.productUPC.set(productUPC); }

    public String getProductName() { return productName.get(); }
    public void setProductName(String productName) { this.productName.set(productName); }

    public Category getProductCategory() { return productCategory.get(); }
    public void setProductCategory(Category productCategory) { this.productCategory.set(productCategory); }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }

    public BigDecimal getPrice() { return price.get(); }
    public void setPrice(BigDecimal price) { this.price.set(price); }

    public int getNumOfSold() { return numOfSoldProduct.get(); }
    public void setNumOfSold(int num) { this.numOfSoldProduct.set(num); }

    @Override
    public String toString() {
        return "Product Details:\n" +
                "  UPC: " + getProductUPC() + "\n" +
                "  Name: " + getProductName() + "\n" +
                "  Category: " + getProductCategory() + "\n" +
                "  Quantity: " + getQuantity() + "\n" +
                "  Price: $" + getPrice() + "\n" +
                "-------------------------------------\n";
    }

    public ObservableValue<BigDecimal> productPriceProperty() {
        return price;
    }

    public IntegerProperty productQuantityProperty() {
        return quantity;
    }
}