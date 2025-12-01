package com.cqu.exp02.dao;

import com.cqu.exp02.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RolePermissionDao {
    
    public boolean grantPermissionToRole(Long roleId, Long permissionId) throws SQLException {
        String sql = "INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roleId);
            stmt.setLong(2, permissionId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean revokePermissionFromRole(Long roleId, Long permissionId) throws SQLException {
        String sql = "DELETE FROM role_permissions WHERE role_id = ? AND permission_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roleId);
            stmt.setLong(2, permissionId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean revokeAllPermissionsFromRole(Long roleId) throws SQLException {
        String sql = "DELETE FROM role_permissions WHERE role_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roleId);
            
            return stmt.executeUpdate() > 0;
        }
    }
}