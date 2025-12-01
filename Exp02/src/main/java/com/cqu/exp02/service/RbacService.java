

package com.cqu.exp02.service;

import com.cqu.exp02.dao.*;
import com.cqu.exp02.entity.*;
import com.cqu.exp02.util.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RbacService {
    private UserDao userDao = new UserDao();
    private RoleDao roleDao = new RoleDao();
    private PermissionDao permissionDao = new PermissionDao();
    private UserRoleDao userRoleDao = new UserRoleDao();
    private RolePermissionDao rolePermissionDao = new RolePermissionDao();
    
    // User management
    public User createUser(String username, String password, String email) throws SQLException {
        User user = new User(username, password, email);
        User savedUser = userDao.save(user);
        LoggerUtil.info("Created user: " + username + " with ID: " + savedUser.getId());
        return savedUser;
    }
    
    public Optional<User> findUserById(Long id) throws SQLException {
        return userDao.findById(id);
    }
    
    public Optional<User> findUserByUsername(String username) throws SQLException {
        return userDao.findByUsername(username);
    }
    
    public List<User> getAllUsers() throws SQLException {
        return userDao.findAll();
    }
    
    public boolean updateUser(User user) throws SQLException {
        boolean result = userDao.update(user);
        if (result) {
            LoggerUtil.info("Updated user: " + user.getUsername() + " with ID: " + user.getId());
        }
        return result;
    }
    
    public boolean deleteUser(Long userId) throws SQLException {
        Optional<User> userOpt = userDao.findById(userId);
        boolean result = userDao.deleteById(userId);
        if (result && userOpt.isPresent()) {
            LoggerUtil.info("Deleted user: " + userOpt.get().getUsername() + " with ID: " + userId);
        }
        return result;
    }
    
    // Role management
    public Role createRole(String name, String description) throws SQLException {
        Role role = new Role(name, description);
        Role savedRole = roleDao.save(role);
        LoggerUtil.info("Created role: " + name + " with ID: " + savedRole.getId());
        return savedRole;
    }
    
    public Optional<Role> findRoleById(Long id) throws SQLException {
        return roleDao.findById(id);
    }
    
    public Optional<Role> findRoleByName(String name) throws SQLException {
        return roleDao.findByName(name);
    }
    
    public List<Role> getAllRoles() throws SQLException {
        return roleDao.findAll();
    }
    
    public boolean updateRole(Role role) throws SQLException {
        boolean result = roleDao.update(role);
        if (result) {
            LoggerUtil.info("Updated role: " + role.getName() + " with ID: " + role.getId());
        }
        return result;
    }
    
    public boolean deleteRole(Long roleId) throws SQLException {
        Optional<Role> roleOpt = roleDao.findById(roleId);
        boolean result = roleDao.deleteById(roleId);
        if (result && roleOpt.isPresent()) {
            LoggerUtil.info("Deleted role: " + roleOpt.get().getName() + " with ID: " + roleId);
        }
        return result;
    }
    
    // Permission management
    public Permission createPermission(String name, String description, String resource, String action) throws SQLException {
        Permission permission = new Permission(name, description, resource, action);
        Permission savedPermission = permissionDao.save(permission);
        LoggerUtil.info("Created permission: " + name + " with ID: " + savedPermission.getId());
        return savedPermission;
    }
    
    public Optional<Permission> findPermissionById(Long id) throws SQLException {
        return permissionDao.findById(id);
    }
    
    public Optional<Permission> findPermissionByName(String name) throws SQLException {
        return permissionDao.findByName(name);
    }
    
    public List<Permission> getAllPermissions() throws SQLException {
        return permissionDao.findAll();
    }
    
    public boolean updatePermission(Permission permission) throws SQLException {
        boolean result = permissionDao.update(permission);
        if (result) {
            LoggerUtil.info("Updated permission: " + permission.getName() + " with ID: " + permission.getId());
        }
        return result;
    }
    
    public boolean deletePermission(Long permissionId) throws SQLException {
        Optional<Permission> permissionOpt = permissionDao.findById(permissionId);
        boolean result = permissionDao.deleteById(permissionId);
        if (result && permissionOpt.isPresent()) {
            LoggerUtil.info("Deleted permission: " + permissionOpt.get().getName() + " with ID: " + permissionId);
        }
        return result;
    }
    
    // User-Role assignments
    public boolean assignRoleToUser(Long userId, Long roleId) throws SQLException {
        Optional<User> userOpt = userDao.findById(userId);
        Optional<Role> roleOpt = roleDao.findById(roleId);
        
        if (!userOpt.isPresent() || !roleOpt.isPresent()) {
            LoggerUtil.warn("Attempt to assign role to user failed: User or role not found");
            return false;
        }
        
        boolean result = userRoleDao.assignRoleToUser(userId, roleId);
        if (result) {
            LoggerUtil.info("Assigned role: " + roleOpt.get().getName() + " to user: " + userOpt.get().getUsername());
        }
        return result;
    }
    
    public boolean removeRoleFromUser(Long userId, Long roleId) throws SQLException {
        Optional<User> userOpt = userDao.findById(userId);
        Optional<Role> roleOpt = roleDao.findById(roleId);
        
        if (!userOpt.isPresent() || !roleOpt.isPresent()) {
            LoggerUtil.warn("Attempt to remove role from user failed: User or role not found");
            return false;
        }
        
        boolean result = userRoleDao.removeRoleFromUser(userId, roleId);
        if (result) {
            LoggerUtil.info("Removed role: " + roleOpt.get().getName() + " from user: " + userOpt.get().getUsername());
        }
        return result;
    }
    
    public boolean removeAllRolesFromUser(Long userId) throws SQLException {
        Optional<User> userOpt = userDao.findById(userId);
        if (!userOpt.isPresent()) {
            LoggerUtil.warn("Attempt to remove all roles from user failed: User not found");
            return false;
        }
        
        boolean result = userRoleDao.removeAllRolesFromUser(userId);
        if (result) {
            LoggerUtil.info("Removed all roles from user: " + userOpt.get().getUsername());
        }
        return result;
    }
    
    // Role-Permission assignments
    public boolean grantPermissionToRole(Long roleId, Long permissionId) throws SQLException {
        Optional<Role> roleOpt = roleDao.findById(roleId);
        Optional<Permission> permissionOpt = permissionDao.findById(permissionId);
        
        if (!roleOpt.isPresent() || !permissionOpt.isPresent()) {
            LoggerUtil.warn("Attempt to grant permission to role failed: Role or permission not found");
            return false;
        }
        
        boolean result = rolePermissionDao.grantPermissionToRole(roleId, permissionId);
        if (result) {
            LoggerUtil.info("Granted permission: " + permissionOpt.get().getName() + " to role: " + roleOpt.get().getName());
        }
        return result;
    }
    
    public boolean revokePermissionFromRole(Long roleId, Long permissionId) throws SQLException {
        Optional<Role> roleOpt = roleDao.findById(roleId);
        Optional<Permission> permissionOpt = permissionDao.findById(permissionId);
        
        if (!roleOpt.isPresent() || !permissionOpt.isPresent()) {
            LoggerUtil.warn("Attempt to revoke permission from role failed: Role or permission not found");
            return false;
        }
        
        boolean result = rolePermissionDao.revokePermissionFromRole(roleId, permissionId);
        if (result) {
            LoggerUtil.info("Revoked permission: " + permissionOpt.get().getName() + " from role: " + roleOpt.get().getName());
        }
        return result;
    }
    
    public boolean revokeAllPermissionsFromRole(Long roleId) throws SQLException {
        Optional<Role> roleOpt = roleDao.findById(roleId);
        if (!roleOpt.isPresent()) {
            LoggerUtil.warn("Attempt to revoke all permissions from role failed: Role not found");
            return false;
        }
        
        boolean result = rolePermissionDao.revokeAllPermissionsFromRole(roleId);
        if (result) {
            LoggerUtil.info("Revoked all permissions from role: " + roleOpt.get().getName());
        }
        return result;
    }
    
    // Permission checking methods
    public boolean checkPermission(Long userId, String resource, String action) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users u " +
                     "JOIN user_roles ur ON u.id = ur.user_id " +
                     "JOIN role_permissions rp ON ur.role_id = rp.role_id " +
                     "JOIN permissions p ON rp.permission_id = p.id " +
                     "WHERE u.id = ? AND p.resource = ? AND p.action = ?";
        
        try (Connection conn = com.cqu.exp02.util.DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setString(2, resource);
            stmt.setString(3, action);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public List<String> getUserPermissions(Long userId) throws SQLException {
        List<String> permissions = new ArrayList<>();
        String sql = "SELECT DISTINCT p.resource, p.action FROM users u " +
                     "JOIN user_roles ur ON u.id = ur.user_id " +
                     "JOIN role_permissions rp ON ur.role_id = rp.role_id " +
                     "JOIN permissions p ON rp.permission_id = p.id " +
                     "WHERE u.id = ?";
        
        try (Connection conn = com.cqu.exp02.util.DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(rs.getString("resource") + ":" + rs.getString("action"));
                }
            }
        }
        return permissions;
    }
    
    public List<Role> getUserRoles(Long userId) throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.* FROM users u " +
                     "JOIN user_roles ur ON u.id = ur.user_id " +
                     "JOIN roles r ON ur.role_id = r.id " +
                     "WHERE u.id = ?";
        
        try (Connection conn = com.cqu.exp02.util.DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
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
        }
        return roles;
    }
    
    public List<Permission> getRolePermissions(Long roleId) throws SQLException {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT p.* FROM roles r " +
                     "JOIN role_permissions rp ON r.id = rp.role_id " +
                     "JOIN permissions p ON rp.permission_id = p.id " +
                     "WHERE r.id = ?";
        
        try (Connection conn = com.cqu.exp02.util.DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
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
        }
        return permissions;
    }
}