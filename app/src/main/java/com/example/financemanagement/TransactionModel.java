package com.example.financemanagement;

public class TransactionModel {
    private String amount;
    private String description;
    private String transactionDate;
    private String categoryName;
    private String categoryImage;
    private String categoryTypeName;
    private int category;
    private int categoryId;
    private int categoryTypeId;
    private int idTransaction;


    public TransactionModel(String amount, String description, String transactionDate, String categoryName, String categoryImage, String categoryTypeName, int category, int categoryId, int categoryTypeId, int idTransaction) {
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.categoryTypeName = categoryTypeName;
        this.category = category;
        this.categoryId = categoryId;
        this.categoryTypeId = categoryTypeId;
        this.idTransaction = idTransaction;
    }

    // Getters and Setters
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransactionDate() {
//        transactionDate có dạng 2024-12-16T00:06:50.064022+07:00 giờ muốn lấy ngày thì cắt chuỗi từ 0 đến 9
        return transactionDate.substring(0, 10);
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryTypeName() {
        return categoryTypeName;
    }

    public void setCategoryTypeName(String categoryTypeName) {
        this.categoryTypeName = categoryTypeName;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategoryTypeId() {
        return categoryTypeId;
    }

    public void setCategoryTypeId(int categoryTypeId) {
        this.categoryTypeId = categoryTypeId;
    }

    public int getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(int idTransaction) {
        this.idTransaction = idTransaction;
    }
}

