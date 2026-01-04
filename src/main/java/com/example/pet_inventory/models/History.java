package com.example.pet_inventory.models;

import javafx.beans.property.*;
import java.math.BigDecimal;

/**
 * Represents a record in order history.
 * Uses JavaFX properties to allow TableView binding and dynamic updates.
 */
public class History {

    // Properties for TableView binding
    private final StringProperty orderID;
    private final StringProperty productName;
    private final IntegerProperty quantity;
    private final ObjectProperty<BigDecimal> subtotal;
    private final StringProperty dateCreated;
    private final StringProperty paymentMethod;

    /**
     * Constructor for History record.
     *
     * @param orderID       Order ID
     * @param productName   Product name
     * @param quantity      Quantity sold
     * @param subtotal      Subtotal amount
     * @param dateCreated   Date of order
     * @param paymentMethod Payment method used
     */
    public History(String orderID, String productName, int quantity,
                   BigDecimal subtotal, String dateCreated, String paymentMethod) {

        this.orderID = new SimpleStringProperty(orderID);
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.subtotal = new SimpleObjectProperty<>(subtotal);
        this.dateCreated = new SimpleStringProperty(dateCreated);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
    }

    // ---------------- Getters & Setters with Properties ----------------

    public String getOrderID() { return orderID.get(); }
    public void setOrderID(String orderID) { this.orderID.set(orderID); }
    public StringProperty orderIDProperty() { return orderID; }

    public String getProductName() { return productName.get(); }
    public void setProductName(String productName) { this.productName.set(productName); }
    public StringProperty productNameProperty() { return productName; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public IntegerProperty quantityProperty() { return quantity; }

    public BigDecimal getSubtotal() { return subtotal.get(); }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal.set(subtotal); }
    public ObjectProperty<BigDecimal> subtotalProperty() { return subtotal; }

    public String getDateCreated() { return dateCreated.get(); }
    public void setDateCreated(String dateCreated) { this.dateCreated.set(dateCreated); }
    public StringProperty dateCreatedProperty() { return dateCreated; }

    public String getPaymentMethod() { return paymentMethod.get(); }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod.set(paymentMethod); }
    public StringProperty paymentMethodProperty() { return paymentMethod; }
}