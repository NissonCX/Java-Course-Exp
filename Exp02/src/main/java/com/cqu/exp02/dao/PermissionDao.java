package com.cqu.exp02.dao;

import com.cqu.exp02.entity.Permission;
import com.cqu.exp02.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PermissionDao {
    
    public Permission save(Permission permission) throws SQLException {
        String sql = "INSERT INTO permissions (name, description, resource, action, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, permission.getName());
            stmt.setString(2, permission.getDescription());
            stmt.setString(3, permission.getResource());
            stmt.setString(4, permission.getAction());
            stmt.setTimestamp(5, Timestamp.valueOf(permission.getCreatedAt()));
            stmt.setTimestamp(6, Timestamp.valueOf(permission.getUpdatedAt()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating permission failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    permission.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating permission failed, no ID obtained.");
                }
            }
        }
        return permission;
    }
    
    public Optional<Permission> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM permissions WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Permission permission = new Permission();
                    permission.setId(rs.getLong("id"));
                    permission.setName(rs.getString("name"));
                    permission.setDescription(rs.getString("description"));
                    permission.setResource(rs.getString("resource"));
                    permission.setAction(rs.getString("action"));
                    permission.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    permission.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return Optional.of(permission);
                }
            }
        }
        return Optional.empty();
    }
    
    public Optional<Permission> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM permissions WHERE name = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Permission permission = new Permission();
                    permission.setId(rs.getLong("id"));
                    permission.setName(rs.getString("name"));
                    permission.setDescription(rs.getString("description"));
                    permission.setResource(rs.getString("resource"));
                    permission.setAction(rs.getString("action"));
                    permission.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    permission.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return Optional.of(permission);
                }
            }
        }
        return Optional.empty();
    }
    
    public List<Permission> findAll() throws SQLException {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT * FROM permissions";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Permission permission = new Permission();
                permission.setId(rs.getLong("id"));
                permission.setName(rs.getString("name"));
                permission.setDescription(rs.getString("description"));
                permission.setResource(rs.getString("resource"));
                permission.setAction(rs.getString("action"));
                permission.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                permission.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                permissions.add(permission);
            }
        }
        return permissions;
    }
    
    public boolean update(Permission permission) throws SQLException {
        String sql = "UPDATE permissions SET name = ?, description = ?, resource = ?, action = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, permission.getName());
            stmt.setString(2, permission.getDescription());
            stmt.setString(3, permission.getResource());
            stmt.setString(4, permission.getAction());
            stmt.setTimestamp(5, Timestamp.valueOf(permission.getUpdatedAt()));
            stmt.setLong(6, permission.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM permissions WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}