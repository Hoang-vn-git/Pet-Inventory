package com.example.pet_inventory.models;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {

    // Fields
    private final StringProperty orderID;
    private final ObjectProperty<LocalDateTime> dateCreated;
    private final ObjectProperty<BigDecimal> totalAmount;
    private final StringProperty paymentMethod;

    // Constructor
    public Order(String orderID, LocalDateTime dateCreated, BigDecimal totalAmount, String paymentMethod) {
        this.orderID = new SimpleStringProperty(orderID);
        this.dateCreated = new SimpleObjectProperty<>(dateCreated);
        this.totalAmount = new SimpleObjectProperty<>(totalAmount);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
    }

    // ===== Getters & Setters =====

    public String getOrderID() {
        return orderID.get();
    }

    public void setOrderID(String orderID) {
        this.orderID.set(orderID);
    }

    public StringProperty orderIDProperty() {
        return orderID;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated.get();
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated.set(dateCreated);
    }

    public ObjectProperty<LocalDateTime> dateCreatedProperty() {
        return dateCreated;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount.get();
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount.set(totalAmount);
    }

    public ObjectProperty<BigDecimal> totalAmountProperty() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod.get();
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public StringProperty paymentMethodProperty() {
        return paymentMethod;
    }
}