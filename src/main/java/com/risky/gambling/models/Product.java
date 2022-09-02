package com.risky.gambling.models;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.risky.gambling.BaseQueries;
import com.risky.gambling.utils.Helpers;

@Component
public class Product extends BaseQueries {
    private static String table = "products";
    private static String[] columns = {"id", "name", "description", "thumbnail", "price", "qty", "created_at", "status"};

    private int id;
    private String name;
    private String description;
    private String thumbnail;
    private DecimalFormat price;
    private int qty;
    private LocalDateTime createdAt;
    private int status;

    public Product() {
        super(table, columns);
    }
    
    public Product(int id, String name, String description, String thumbnail, DecimalFormat price, int qty, LocalDateTime createdAt, int status) {
        super(table, columns);
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.price = price; 
        this.qty = qty;
        this.createdAt = createdAt; 
        this.status = status;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setTitle(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }
    public void setStartDate(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    public DecimalFormat getPrice() {
        return price;
    }
    public void setEndDate(DecimalFormat price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }
    public void setQty(int qty) {
        this.qty = qty;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedAtToString() {
        return Helpers.formatDateTime(createdAt);
    }
    
}
