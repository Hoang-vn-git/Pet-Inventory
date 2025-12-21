package com.example.pet_inventory.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import java.math.BigDecimal;

public class OrderItem {
    private StringProperty checkoutID;
    private StringProperty orderID;
    private StringProperty productUPC;
    private StringProperty productName;
    private ObjectProperty<BigDecimal> productPrice;
    private IntegerProperty productQuantity;
    private ObjectProperty<BigDecimal> subTotal;

    public OrderItem(StringProperty checkoutID, StringProperty orderID, StringProperty productUPC, StringProperty productName, ObjectProperty<BigDecimal> productPrice, IntegerProperty productQuantity, ObjectProperty<BigDecimal> subTotal) {
        this.checkoutID = checkoutID;
        this.orderID = orderID;
        this.productUPC = productUPC;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.subTotal = subTotal;
    }

    public String getCheckoutID() {
        return checkoutID.get();
    }

    public StringProperty checkoutIDProperty() {
        return checkoutID;
    }

    public void setCheckoutID(String checkoutID) {
        this.checkoutID.set(checkoutID);
    }

    public String getOrderID() {
        return orderID.get();
    }

    public StringProperty orderIDProperty() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID.set(orderID);
    }

    public String getProductUPC() {
        return productUPC.get();
    }

    public StringProperty productUPCProperty() {
        return productUPC;
    }

    public void setProductUPC(String productUPC) {
        this.productUPC.set(productUPC);
    }

    public String getProductName() {
        return productName.get();
    }

    public StringProperty productNameProperty() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public BigDecimal getProductPrice() {
        return productPrice.get();
    }

    public ObjectProperty<BigDecimal> productPriceProperty() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice.set(productPrice);
    }

    public int getProductQuantity() {
        return productQuantity.get();
    }

    public IntegerProperty productQuantityProperty() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity.set(productQuantity);
    }

    public BigDecimal getSubTotal() {
        return subTotal.get();
    }

    public ObjectProperty<BigDecimal> subTotalProperty() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal.set(subTotal);
    }
}
