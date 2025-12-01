package com.cqu.exp02.dao;

import com.cqu.exp02.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserRoleDao {
    
    public boolean assignRoleToUser(Long userId, Long roleId) throws SQLException {
        String sql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, roleId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean removeRoleFromUser(Long userId, Long roleId) throws SQLException {
        String sql = "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, roleId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean removeAllRolesFromUser(Long userId) throws SQLException {
        String sql = "DELETE FROM user_roles WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            return stmt.executeUpdate() > 0;
        }
    }
}