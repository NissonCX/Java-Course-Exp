package com.cqu.exp02.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Permission {
    private Long id;
    private String name;
    private String description;
    private String resource;
    private String action;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Permission() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Permission(String name, String description, String resource, String action) {
        this.name = name;
        this.description = description;
        this.resource = resource;
        this.action = action;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getResource() {
        return resource;
    }
    
    public String getAction() {
        return action;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setResource(String resource) {
        this.resource = resource;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}