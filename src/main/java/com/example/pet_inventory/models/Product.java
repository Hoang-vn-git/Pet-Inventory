package com.example.pet_inventory.models;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.math.BigDecimal;

/**
 * Represents a product in the inventory.
 * Uses JavaFX Properties for TableView binding and dynamic updates.
 */
public class Product {

    // ---------------- Fields ----------------
    private final StringProperty productUPC;         // Unique product code
    private final StringProperty productName;        // Product name
    private final ObjectProperty<Category> productCategory; // Product category
    private final IntegerProperty quantity;          // Available quantity
    private final ObjectProperty<BigDecimal> price; // Unit price

    /**
     * Constructor
     *
     * @param productUPC      Unique product code
     * @param productName     Product name
     * @param productCategory Category enum
     * @param quantity        Quantity available
     * @param price           Unit price
     */
    public Product(String productUPC, String productName, Category productCategory, int quantity, BigDecimal price) {
        this.productUPC = new SimpleStringProperty(productUPC);
        this.productName = new SimpleStringProperty(productName);
        this.productCategory = new SimpleObjectProperty<>(productCategory);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleObjectProperty<>(price);
    }

    // ---------------- Property getters (for TableView binding) ----------------
    public StringProperty productUPCProperty() { return productUPC; }
    public StringProperty productNameProperty() { return productName; }
    public ObjectProperty<Category> productCategoryProperty() { return productCategory; }
    public IntegerProperty quantityProperty() { return quantity; }
    public ObjectProperty<BigDecimal> priceProperty() { return price; }

    // ---------------- Standard getters / setters ----------------
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

    // ---------------- Helper / UI Methods ----------------

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

    // Additional property getters for TableView (if column binding requires)
    public ObservableValue<BigDecimal> productPricePropertyObservable() { return price; }
    public IntegerProperty productQuantityPropertyObservable() { return quantity; }
}