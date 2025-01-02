package com.example.financemanagement;

public class HeaderModel {
    private String date;
    private double totalSpending;
    private double totalIncome;

    public HeaderModel(String date, double totalSpending, double totalIncome) {
        this.date = date;
        this.totalSpending = totalSpending;
        this.totalIncome = totalIncome;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalSpending() {
        return totalSpending;
    }

    public void setTotalSpending(double totalSpending) {
        this.totalSpending = totalSpending;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }
}