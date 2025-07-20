package com.example.orderclothes.models;


public class ReportItem {
    private String dateLabel;
    private int orderCount;
    private double totalRevenue;


    public ReportItem(String dateLabel, int orderCount, double totalRevenue) {
        this.dateLabel = dateLabel;
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
    }


    public String getDateLabel() {
        return dateLabel;
    }


    public int getOrderCount() {
        return orderCount;
    }


    public double getTotalRevenue() {
        return totalRevenue;
    }
}









