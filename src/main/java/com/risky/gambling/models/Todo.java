package com.risky.gambling.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.risky.gambling.BaseQueries;
import com.risky.gambling.utils.Helpers;

@Component
public class Todo extends BaseQueries {
    private static String table = "todo";
    private static String[] columns = {"id", "title", "description", "start_date", "end_date", "created_at", "status"};

    private int id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private int status;

    public Todo() {
        super(table, columns);
    }
    
    public Todo(int id, String title, String description, LocalDate startDate, LocalDate endDate, LocalDateTime createdAt, int status) {
        super(table, columns);
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate; 
        this.createdAt = createdAt; 
        this.status = status;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
    
    public String getEndDateToString(){
        return Helpers.formatDate(endDate);
    }

    public String getStartDateToString() {
        return Helpers.formatDate(startDate);
    }
}
