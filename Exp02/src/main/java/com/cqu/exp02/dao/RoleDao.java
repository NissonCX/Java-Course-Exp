package com.cqu.exp02.dao;

import com.cqu.exp02.entity.Role;
import com.cqu.exp02.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleDao {
    
    public Role save(Role role) throws SQLException {
        String sql = "INSERT INTO roles (name, description, created_at, updated_at) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, role.getName());
            stmt.setString(2, role.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(role.getCreatedAt()));
            stmt.setTimestamp(4, Timestamp.valueOf(role.getUpdatedAt()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating role failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    role.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating role failed, no ID obtained.");
                }
            }
        }
        return role;
    }
    
    public Optional<Role> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM roles WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setId(rs.getLong("id"));
                    role.setName(rs.getString("name"));
                    role.setDescription(rs.getString("description"));
                    role.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    role.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return Optional.of(role);
                }
            }
        }
        return Optional.empty();
    }
    
    public Optional<Role> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM roles WHERE name = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setId(rs.getLong("id"));
                    role.setName(rs.getString("name"));
                    role.setDescription(rs.getString("description"));
                    role.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    role.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return Optional.of(role);
                }
            }
        }
        return Optional.empty();
    }
    
    public List<Role> findAll() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getLong("id"));
                role.setName(rs.getString("name"));
                role.setDescription(rs.getString("description"));
                role.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                role.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                roles.add(role);
            }
        }
        return roles;
    }
    
    public boolean update(Role role) throws SQLException {
        String sql = "UPDATE roles SET name = ?, description = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, role.getName());
            stmt.setString(2, role.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(role.getUpdatedAt()));
            stmt.setLong(4, role.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM roles WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}