package com.example.pet_inventory.models;

import javafx.beans.property.*;

import java.math.BigDecimal;

/**
 * Represents an item in an order (cart).
 * Uses JavaFX Properties for TableView binding and dynamic updates.
 */
public class OrderItem {

    // ---------------- Fields ----------------
    private final StringProperty id;               // Unique checkout item ID
    private final StringProperty orderID;          // Associated order ID
    private final StringProperty productUPC;       // Product UPC code
    private final StringProperty productName;      // Product name
    private final ObjectProperty<BigDecimal> productPrice;   // Unit price
    private final IntegerProperty productQuantity;           // Quantity
    private final ObjectProperty<BigDecimal> subTotal;       // Quantity * price

    /**
     * Constructor for OrderItem.
     *
     * @param id             Unique checkout ID
     * @param orderID        Associated order ID
     * @param productUPC     Product UPC code
     * @param productName    Product name
     * @param productPrice   Price per unit
     * @param productQuantity Quantity purchased
     * @param subTotal       Total price for this item
     */
    public OrderItem(StringProperty id,
                     StringProperty orderID,
                     StringProperty productUPC,
                     StringProperty productName,
                     ObjectProperty<BigDecimal> productPrice,
                     IntegerProperty productQuantity,
                     ObjectProperty<BigDecimal> subTotal) {
        this.id = id;
        this.orderID = orderID;
        this.productUPC = productUPC;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.subTotal = subTotal;
    }

    // ---------------- Getters & Setters ----------------

    public String getCheckoutID() { return id.get(); }
    public void setCheckoutID(String checkoutID) { this.id.set(checkoutID); }
    public StringProperty checkoutIDProperty() { return id; }

    public String getOrderID() { return orderID.get(); }
    public void setOrderID(String orderID) { this.orderID.set(orderID); }
    public StringProperty orderIDProperty() { return orderID; }

    public String getProductUPC() { return productUPC.get(); }
    public void setProductUPC(String productUPC) { this.productUPC.set(productUPC); }
    public StringProperty productUPCProperty() { return productUPC; }

    public String getProductName() { return productName.get(); }
    public void setProductName(String productName) { this.productName.set(productName); }
    public StringProperty productNameProperty() { return productName; }

    public BigDecimal getProductPrice() { return productPrice.get(); }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice.set(productPrice); }
    public ObjectProperty<BigDecimal> productPriceProperty() { return productPrice; }

    public int getProductQuantity() { return productQuantity.get(); }
    public void setProductQuantity(int productQuantity) { this.productQuantity.set(productQuantity); }
    public IntegerProperty productQuantityProperty() { return productQuantity; }

    public BigDecimal getSubTotal() { return subTotal.get(); }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal.set(subTotal); }
    public ObjectProperty<BigDecimal> subTotalProperty() { return subTotal; }
}