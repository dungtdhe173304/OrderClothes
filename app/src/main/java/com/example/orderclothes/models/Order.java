package com.example.orderclothes.models;


import java.util.List;


public class Order {
    private int orderId;
    private String orderNumber;
    private int userId; // từ OrderActivity
    private String orderDate; // từ OrderActivity
    private String customerName;
    private String customerPhone;
    private String shippingAddress;
    private double subtotal;
    private double shippingFee;
    private double totalAmount;
    private String status;
    private List<com.example.orderclothes.models.OrderItem> orderItems; // từ OrderActivity


    public Order() {
    }


    public Order(int orderId, String orderNumber, int userId, String orderDate, String customerName,
                 String customerPhone, String shippingAddress, double subtotal, double shippingFee,
                 double totalAmount, String status, List<com.example.orderclothes.models.OrderItem> orderItems) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.orderDate = orderDate;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.shippingAddress = shippingAddress;
        this.subtotal = subtotal;
        this.shippingFee = shippingFee;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderItems = orderItems;
    }


    public int getOrderId() {
        return orderId;
    }


    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }


    public String getOrderNumber() {
        return orderNumber;
    }


    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }


    public int getUserId() {
        return userId;
    }


    public void setUserId(int userId) {
        this.userId = userId;
    }


    public String getOrderDate() {
        return orderDate;
    }


    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }


    public String getCustomerName() {
        return customerName;
    }


    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public String getCustomerPhone() {
        return customerPhone;
    }


    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }


    public String getShippingAddress() {
        return shippingAddress;
    }


    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }


    public double getSubtotal() {
        return subtotal;
    }


    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }


    public double getShippingFee() {
        return shippingFee;
    }


    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }


    public double getTotalAmount() {
        return totalAmount;
    }


    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }
    public Order(int orderId, String orderNumber, String customerName, String customerPhone,
                 String shippingAddress, double subtotal, double shippingFee,
                 double totalAmount, String status) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.shippingAddress = shippingAddress;
        this.subtotal = subtotal;
        this.shippingFee = shippingFee;
        this.totalAmount = totalAmount;
        this.status = status;
    }


    public List<com.example.orderclothes.models.OrderItem> getOrderItems() {
        return orderItems;
    }


    public void setOrderItems(List<com.example.orderclothes.models.OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}





