package com.example.pet_inventory.models;

import javafx.beans.property.*;
import java.math.BigDecimal;

/**
 * Represents a customer order.
 * Uses JavaFX Properties for TableView binding and dynamic updates.
 */
public class Order {

    // ---------------- Fields ----------------
    private final StringProperty orderID;
    private final StringProperty dateCreated;
    private final ObjectProperty<BigDecimal> totalAmount;
    private final StringProperty paymentMethod;

    /**
     * Constructor for Order.
     *
     * @param orderID       Unique order ID
     * @param dateCreated   Order creation date as String
     * @param totalAmount   Total order amount
     * @param paymentMethod Payment method used
     */
    public Order(String orderID, String dateCreated, BigDecimal totalAmount, String paymentMethod) {
        this.orderID = new SimpleStringProperty(orderID);
        this.dateCreated = new SimpleStringProperty(dateCreated);
        this.totalAmount = new SimpleObjectProperty<>(totalAmount);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
    }

    // ---------------- Getters & Setters ----------------

    public String getOrderID() { return orderID.get(); }
    public void setOrderID(String orderID) { this.orderID.set(orderID); }
    public StringProperty orderIDProperty() { return orderID; }

    public String getDateCreated() { return dateCreated.get(); }
    public void setDateCreated(String dateCreated) { this.dateCreated.set(dateCreated); }
    public StringProperty dateCreatedProperty() { return dateCreated; }

    public BigDecimal getTotalAmount() { return totalAmount.get(); }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount.set(totalAmount); }
    public ObjectProperty<BigDecimal> totalAmountProperty() { return totalAmount; }

    public String getPaymentMethod() { return paymentMethod.get(); }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod.set(paymentMethod); }
    public StringProperty paymentMethodProperty() { return paymentMethod; }
}