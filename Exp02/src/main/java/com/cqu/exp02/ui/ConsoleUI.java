package com.cqu.exp02.ui;

import com.cqu.exp02.service.RbacService;
import com.cqu.exp02.entity.User;
import com.cqu.exp02.entity.Role;
import com.cqu.exp02.entity.Permission;
import com.cqu.exp02.util.LoggerUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUI {
    private RbacService rbacService = new RbacService();
    private Scanner scanner = new Scanner(System.in);
    private User currentUser = null;
    
    public void start() {
        System.out.println("=====================================");
        System.out.println("      欢迎使用RBAC权限管理系统      ");
        System.out.println("=====================================");
        
        // Require login before accessing the system
        if (!login()) {
            System.out.println("登录失败次数过多，系统退出。");
            return;
        }
        
        while (true) {
            showMainMenu();
            int choice = getIntInput("请选择一个选项: ");
            
            switch (choice) {
                case 1:
                    handleUserManagement();
                    break;
                case 2:
                    handleRoleManagement();
                    break;
                case 3:
                    handlePermissionManagement();
                    break;
                case 4:
                    changePassword();
                    break;
                case 0:
                    System.out.println("感谢您使用RBAC权限管理系统！");
                    return;
                default:
                    System.out.println("无效选项，请重试。");
            }
        }
    }
    
    private boolean login() {
        for (int attempts = 3; attempts > 0; attempts--) {
            System.out.print("请输入用户名: ");
            String username = scanner.nextLine();
            
            System.out.print("请输入密码: ");
            String password = scanner.nextLine();
            
            try {
                Optional<User> userOpt = rbacService.findUserByUsername(username);
                if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
                    currentUser = userOpt.get();
                    System.out.println("登录成功！欢迎，" + currentUser.getUsername() + "。");
                    LoggerUtil.info("用户登录成功: " + username);
                    return true;
                } else {
                    System.out.println("用户名或密码错误，您还有" + (attempts - 1) + "次机会。");
                    LoggerUtil.warn("用户登录失败: " + username);
                }
            } catch (SQLException e) {
                System.out.println("登录时发生错误: " + e.getMessage());
                LoggerUtil.error("登录时发生数据库错误: " + e.getMessage());
            }
        }
        return false;
    }
    
    private void showMainMenu() {
        System.out.println("\n========== 主菜单 ==========");
        System.out.println("当前用户: " + currentUser.getUsername());
        System.out.println("1. 用户管理");
        System.out.println("2. 角色管理");
        System.out.println("3. 权限管理");
        System.out.println("4. 修改密码");
        System.out.println("0. 退出");
        System.out.println("===============================");
        System.out.print("请选择一个选项: ");
    }
    
    private void handleUserManagement() {
        try {
            // Check if current user has permission to manage users
            if (!rbacService.checkPermission(currentUser.getId(), "用户", "查看")) {
                System.out.println("您没有权限访问用户管理功能。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试访问无权限的功能: 用户管理");
                return;
            }
        } catch (SQLException e) {
            System.out.println("权限检查时发生错误: " + e.getMessage());
            return;
        }
        
        while (true) {
            showUserMenu();
            int choice = getIntInput("请输入您的选择: ");
            
            switch (choice) {
                case 1:
                    createUser();
                    break;
                case 2:
                    viewAllUsers();
                    break;
                case 3:
                    updateUser();
                    break;
                case 4:
                    deleteUser();
                    break;
                case 5:
                    checkUserPermissions();
                    break;
                case 6:
                    assignRoleToUser();
                    break;
                case 7:
                    removeRoleFromUser();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选项，请重试。");
            }
        }
    }
    
    private void showUserMenu() {
        System.out.println("\n---------- 用户管理 ----------");
        System.out.println("1. 创建用户");
        System.out.println("2. 查看所有用户");
        System.out.println("3. 更新用户");
        System.out.println("4. 删除用户");
        System.out.println("5. 检查用户权限");
        System.out.println("6. 分配角色给用户");
        System.out.println("7. 从用户移除角色");
        System.out.println("0. 返回主菜单");
        System.out.print("请选择一个选项: ");
    }
    
    private void handleRoleManagement() {
        try {
            // Check if current user has permission to manage roles
            if (!rbacService.checkPermission(currentUser.getId(), "角色", "查看")) {
                System.out.println("您没有权限访问角色管理功能。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试访问无权限的功能: 角色管理");
                return;
            }
        } catch (SQLException e) {
            System.out.println("权限检查时发生错误: " + e.getMessage());
            return;
        }
        
        while (true) {
            showRoleMenu();
            int choice = getIntInput("请输入您的选择: ");
            
            switch (choice) {
                case 1:
                    createRole();
                    break;
                case 2:
                    viewAllRoles();
                    break;
                case 3:
                    updateRole();
                    break;
                case 4:
                    deleteRole();
                    break;
                case 5:
                    grantPermissionToRole();
                    break;
                case 6:
                    revokePermissionFromRole();
                    break;
                case 7:
                    viewRolePermissions();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选项，请重试。");
            }
        }
    }
    
    private void showRoleMenu() {
        System.out.println("\n---------- 角色管理 ----------");
        System.out.println("1. 创建角色");
        System.out.println("2. 查看所有角色");
        System.out.println("3. 更新角色");
        System.out.println("4. 删除角色");
        System.out.println("5. 授予角色权限");
        System.out.println("6. 撤销角色权限");
        System.out.println("7. 查看角色权限");
        System.out.println("0. 返回主菜单");
        System.out.print("请选择一个选项: ");
    }
    
    private void handlePermissionManagement() {
        try {
            // Check if current user has permission to manage permissions
            if (!rbacService.checkPermission(currentUser.getId(), "权限", "查看")) {
                System.out.println("您没有权限访问权限管理功能。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试访问无权限的功能: 权限管理");
                return;
            }
        } catch (SQLException e) {
            System.out.println("权限检查时发生错误: " + e.getMessage());
            return;
        }
        
        while (true) {
            showPermissionMenu();
            int choice = getIntInput("请输入您的选择: ");
            
            switch (choice) {
                case 1:
                    createPermission();
                    break;
                case 2:
                    viewAllPermissions();
                    break;
                case 3:
                    updatePermission();
                    break;
                case 4:
                    deletePermission();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选项，请重试。");
            }
        }
    }
    
    private void showPermissionMenu() {
        System.out.println("\n------- 权限管理 -------");
        System.out.println("1. 创建权限");
        System.out.println("2. 查看所有权限");
        System.out.println("3. 更新权限");
        System.out.println("4. 删除权限");
        System.out.println("0. 返回主菜单");
        System.out.println("====================================");
        System.out.print("请选择一个选项: ");
    }
    
    // User management methods
    private void createUser() {
        try {
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "用户", "创建")) {
                System.out.println("您没有权限创建用户。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试创建用户但没有权限");
                return;
            }
            
            System.out.print("请输入用户名: ");
            String username = scanner.nextLine();
            
            System.out.print("请输入密码: ");
            String password = scanner.nextLine();
            
            System.out.print("请输入邮箱: ");
            String email = scanner.nextLine();
            
            User user = rbacService.createUser(username, password, email);
            System.out.println("用户创建成功，ID: " + user.getId());
            LoggerUtil.info("用户 " + currentUser.getUsername() + " 创建了新用户: " + username);
        } catch (SQLException e) {
            System.out.println("创建用户时出错: " + e.getMessage());
        }
    }
    
    private void viewAllUsers() {
        try {
            List<User> users = rbacService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("未找到用户。");
            } else {
                System.out.println("\n--- 所有用户 ---");
                for (User user : users) {
                    System.out.println("ID: " + user.getId() + ", 用户名: " + user.getUsername() + 
                                     ", 邮箱: " + user.getEmail());
                }
            }
        } catch (SQLException e) {
            System.out.println("获取用户时出错: " + e.getMessage());
        }
    }
    
    private void updateUser() {
        try {
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "用户", "更新")) {
                System.out.println("您没有权限更新用户。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试更新用户但没有权限");
                return;
            }
            
            System.out.print("请输入要更新的用户ID: ");
            Long userId = Long.parseLong(scanner.nextLine());
            
            Optional<User> userOpt = rbacService.findUserById(userId);
            if (!userOpt.isPresent()) {
                System.out.println("未找到用户。");
                return;
            }
            
            User user = userOpt.get();
            System.out.println("当前用户名: " + user.getUsername());
            System.out.print("请输入新用户名 (留空保持不变): ");
            String username = scanner.nextLine();
            if (!username.trim().isEmpty()) {
                user.setUsername(username);
            }
            
            System.out.print("请输入新邮箱 (留空保持不变): ");
            String email = scanner.nextLine();
            if (!email.trim().isEmpty()) {
                user.setEmail(email);
            }
            
            if (rbacService.updateUser(user)) {
                System.out.println("用户更新成功。");
                LoggerUtil.info("用户 " + currentUser.getUsername() + " 更新了用户: " + user.getUsername());
            } else {
                System.out.println("用户更新失败。");
            }
        } catch (NumberFormatException e) {
            System.out.println("用户ID格式无效。");
        } catch (SQLException e) {
            System.out.println("更新用户时出错: " + e.getMessage());
        }
    }
    
    private void deleteUser() {
        try {
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "用户", "删除")) {
                System.out.println("您没有权限删除用户。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试删除用户但没有权限");
                return;
            }
            
            System.out.print("请输入要删除的用户ID: ");
            Long userId = Long.parseLong(scanner.nextLine());
            
            // Prevent users from deleting themselves
            if (currentUser.getId().equals(userId)) {
                System.out.println("您不能删除自己的账户。");
                return;
            }
            
            if (rbacService.deleteUser(userId)) {
                System.out.println("用户删除成功。");
                LoggerUtil.info("用户 " + currentUser.getUsername() + " 删除了用户ID: " + userId);
            } else {
                System.out.println("用户删除失败，用户可能不存在。");
            }
        } catch (NumberFormatException e) {
            System.out.println("用户ID格式无效。");
        } catch (SQLException e) {
            System.out.println("删除用户时出错: " + e.getMessage());
        }
    }
    
    // Role management methods
    private void createRole() {
        try {
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "角色", "创建")) {
                System.out.println("您没有权限创建角色。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试创建角色但没有权限");
                return;
            }
            
            System.out.print("请输入角色名称: ");
            String name = scanner.nextLine();
            
            System.out.print("请输入角色描述: ");
            String description = scanner.nextLine();
            
            Role role = rbacService.createRole(name, description);
            System.out.println("角色创建成功，ID: " + role.getId());
            LoggerUtil.info("用户 " + currentUser.getUsername() + " 创建了新角色: " + name);
        } catch (SQLException e) {
            System.out.println("创建角色时出错: " + e.getMessage());
        }
    }
    
    private void viewAllRoles() {
        try {
            List<Role> roles = rbacService.getAllRoles();
            if (roles.isEmpty()) {
                System.out.println("未找到角色。");
            } else {
                System.out.println("\n--- 所有角色 ---");
                for (Role role : roles) {
                    System.out.println("ID: " + role.getId() + ", 名称: " + role.getName() + 
                                     ", 描述: " + role.getDescription());
                }
            }
        } catch (SQLException e) {
            System.out.println("获取角色时出错: " + e.getMessage());
        }
    }
    
    private void updateRole() {
        try {
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "角色", "更新")) {
                System.out.println("您没有权限更新角色。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试更新角色但没有权限");
                return;
            }
            
            System.out.print("请输入要更新的角色ID: ");
            Long roleId = Long.parseLong(scanner.nextLine());
            
            Optional<Role> roleOpt = rbacService.findRoleById(roleId);
            if (!roleOpt.isPresent()) {
                System.out.println("未找到角色。");
                return;
            }
            
            Role role = roleOpt.get();
            System.out.println("当前名称: " + role.getName());
            System.out.print("请输入新名称 (留空保持不变): ");
            String name = scanner.nextLine();
            if (!name.trim().isEmpty()) {
                role.setName(name);
            }
            
            System.out.print("请输入新描述 (留空保持不变): ");
            String description = scanner.nextLine();
            if (!description.trim().isEmpty()) {
                role.setDescription(description);
            }
            
            if (rbacService.updateRole(role)) {
                System.out.println("角色更新成功。");
                LoggerUtil.info("用户 " + currentUser.getUsername() + " 更新了角色: " + role.getName());
            } else {
                System.out.println("角色更新失败。");
            }
        } catch (NumberFormatException e) {
            System.out.println("角色ID格式无效。");
        } catch (SQLException e) {
            System.out.println("更新角色时出错: " + e.getMessage());
        }
    }
    
    private void deleteRole() {
        try {
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "角色", "删除")) {
                System.out.println("您没有权限删除角色。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试删除角色但没有权限");
                return;
            }
            
            System.out.print("请输入要删除的角色ID: ");
            Long roleId = Long.parseLong(scanner.nextLine());
            
            if (rbacService.deleteRole(roleId)) {
                System.out.println("角色删除成功。");
                LoggerUtil.info("用户 " + currentUser.getUsername() + " 删除了角色ID: " + roleId);
            } else {
                System.out.println("角色删除失败，角色可能不存在。");
            }
        } catch (NumberFormatException e) {
            System.out.println("角色ID格式无效。");
        } catch (SQLException e) {
            System.out.println("删除角色时出错: " + e.getMessage());
        }
    }
    
    // Permission management methods
    private void createPermission() {
        try {
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "权限", "创建")) {
                System.out.println("您没有权限创建权限。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试创建权限但没有权限");
                return;
            }
            
            System.out.print("请输入权限名称: ");
            String name = scanner.nextLine();
            
            System.out.print("请输入权限描述: ");
            String description = scanner.nextLine();
            
            System.out.print("请输入资源: ");
            String resource = scanner.nextLine();
            
            System.out.print("请输入操作: ");
            String action = scanner.nextLine();
            
            Permission permission = rbacService.createPermission(name, description, resource, action);
            System.out.println("权限创建成功，ID: " + permission.getId());
            LoggerUtil.info("用户 " + currentUser.getUsername() + " 创建了新权限: " + name);
        } catch (SQLException e) {
            System.out.println("创建权限时出错: " + e.getMessage());
        }
    }
    
    private void viewAllPermissions() {
        try {
            List<Permission> permissions = rbacService.getAllPermissions();
            if (permissions.isEmpty()) {
                System.out.println("未找到权限。");
            } else {
                System.out.println("\n--- 所有权限 ---");
                for (Permission permission : permissions) {
                    System.out.println("ID: " + permission.getId() + ", 名称: " + permission.getName() + 
                                     ", 资源: " + permission.getResource() + ", 操作: " + permission.getAction() +
                                     ", 描述: " + permission.getDescription());
                }
            }
        } catch (SQLException e) {
            System.out.println("获取权限时出错: " + e.getMessage());
        }
    }
    
    private void updatePermission() {
        try {
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "权限", "更新")) {
                System.out.println("您没有权限更新权限。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试更新权限但没有权限");
                return;
            }
            
            System.out.print("请输入要更新的权限ID: ");
            Long permissionId = Long.parseLong(scanner.nextLine());
            
            Optional<Permission> permissionOpt = rbacService.findPermissionById(permissionId);
            if (!permissionOpt.isPresent()) {
                System.out.println("未找到权限。");
                return;
            }
            
            Permission permission = permissionOpt.get();
            System.out.println("当前名称: " + permission.getName());
            System.out.print("请输入新名称 (留空保持不变): ");
            String name = scanner.nextLine();
            if (!name.trim().isEmpty()) {
                permission.setName(name);
            }
            
            System.out.print("请输入新描述 (留空保持不变): ");
            String description = scanner.nextLine();
            if (!description.trim().isEmpty()) {
                permission.setDescription(description);
            }
            
            System.out.print("请输入新资源 (留空保持不变): ");
            String resource = scanner.nextLine();
            if (!resource.trim().isEmpty()) {
                permission.setResource(resource);
            }
            
            System.out.print("请输入新操作 (留空保持不变): ");
            String action = scanner.nextLine();
            if (!action.trim().isEmpty()) {
                permission.setAction(action);
            }
            
            if (rbacService.updatePermission(permission)) {
                System.out.println("权限更新成功。");
                LoggerUtil.info("用户 " + currentUser.getUsername() + " 更新了权限: " + permission.getName());
            } else {
                System.out.println("权限更新失败。");
            }
        } catch (NumberFormatException e) {
            System.out.println("权限ID格式无效。");
        } catch (SQLException e) {
            System.out.println("更新权限时出错: " + e.getMessage());
        }
    }
    
    private void deletePermission() {
        try {
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "权限", "删除")) {
                System.out.println("您没有权限删除权限。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试删除权限但没有权限");
                return;
            }
            
            System.out.print("请输入要删除的权限ID: ");
            Long permissionId = Long.parseLong(scanner.nextLine());
            
            if (rbacService.deletePermission(permissionId)) {
                System.out.println("权限删除成功。");
                LoggerUtil.info("用户 " + currentUser.getUsername() + " 删除了权限ID: " + permissionId);
            } else {
                System.out.println("权限删除失败，权限可能不存在。");
            }
        } catch (NumberFormatException e) {
            System.out.println("权限ID格式无效。");
        } catch (SQLException e) {
            System.out.println("删除权限时出错: " + e.getMessage());
        }
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("输入不能为空，请重新输入。");
                    continue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("输入无效，请输入一个有效的数字。");
            }
        }
    }
    
    // Additional methods for RBAC functionality
    private void changePassword() {
        try {
            System.out.print("请输入当前密码: ");
            String currentPassword = scanner.nextLine();
            
            // Verify current password
            if (!currentUser.getPassword().equals(currentPassword)) {
                System.out.println("当前密码不正确。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试修改密码时输入错误的当前密码");
                return;
            }
            
            System.out.print("请输入新密码: ");
            String newPassword = scanner.nextLine();
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                System.out.println("密码不能为空。");
                return;
            }
            
            System.out.print("请再次输入新密码: ");
            String confirmNewPassword = scanner.nextLine();
            
            if (!newPassword.equals(confirmNewPassword)) {
                System.out.println("两次输入的密码不一致。");
                return;
            }
            
            // Update password
            currentUser.setPassword(newPassword);
            currentUser.setUpdatedAt(java.time.LocalDateTime.now());
            
            if (rbacService.updateUser(currentUser)) {
                System.out.println("密码修改成功。");
                LoggerUtil.info("用户 " + currentUser.getUsername() + " 成功修改密码");
            } else {
                System.out.println("密码修改失败。");
                LoggerUtil.error("用户 " + currentUser.getUsername() + " 修改密码失败");
            }
        } catch (SQLException e) {
            System.out.println("修改密码时发生错误: " + e.getMessage());
            LoggerUtil.error("修改密码时发生数据库错误: " + e.getMessage());
        }
    }
    
    private void checkUserPermissions() {
        try {
            System.out.print("请输入要检查权限的用户ID: ");
            Long userId = Long.parseLong(scanner.nextLine());
            
            Optional<User> userOpt = rbacService.findUserById(userId);
            if (!userOpt.isPresent()) {
                System.out.println("未找到用户。");
                return;
            }
            
            // Check if current user has permission to view other users' permissions
            if (!currentUser.getId().equals(userId)) {
                if (!rbacService.checkPermission(currentUser.getId(), "用户", "查看")) {
                    System.out.println("您没有权限查看其他用户的权限信息。");
                    LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试查看用户 " + userId + " 的权限信息");
                    return;
                }
            }
            
            // Get user permissions
            List<String> permissions = rbacService.getUserPermissions(userId);
            List<Role> roles = rbacService.getUserRoles(userId);
            
            System.out.println("\n--- 用户权限 ---");
            System.out.println("用户: " + userOpt.get().getUsername());
            System.out.println("角色:");
            for (Role role : roles) {
                System.out.println("  - " + role.getName() + ": " + role.getDescription());
            }
            
            System.out.println("权限:");
            if (permissions.isEmpty()) {
                System.out.println("  未找到权限。");
            } else {
                for (String permission : permissions) {
                    System.out.println("  - " + permission);
                }
            }
            
            LoggerUtil.info("检查用户权限: " + userOpt.get().getUsername());
        } catch (NumberFormatException e) {
            System.out.println("用户ID格式无效。");
        } catch (SQLException e) {
            System.out.println("检查用户权限时出错: " + e.getMessage());
            LoggerUtil.error("检查用户权限时出错: " + e.getMessage());
        }
    }
    
    private void assignRoleToUser() {
        try {
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "角色", "分配")) {
                System.out.println("您没有权限为用户分配角色。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试为用户分配角色但没有权限");
                return;
            }
            
            System.out.print("请输入用户ID: ");
            Long userId = Long.parseLong(scanner.nextLine());
            
            System.out.print("请输入角色ID: ");
            Long roleId = Long.parseLong(scanner.nextLine());
            
            boolean result = rbacService.assignRoleToUser(userId, roleId);
            if (result) {
                System.out.println("角色分配给用户成功。");
                LoggerUtil.info("用户 " + currentUser.getUsername() + " 将角色ID: " + roleId + " 分配给用户ID: " + userId);
            } else {
                System.out.println("角色分配给用户失败，请检查用户和角色是否存在。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试分配角色失败。用户ID: " + userId + ", 角色ID: " + roleId);
            }
        } catch (NumberFormatException e) {
            System.out.println("ID格式无效。");
            LoggerUtil.error("assignRoleToUser中ID格式无效: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("分配角色给用户时出错: " + e.getMessage());
            LoggerUtil.error("分配角色给用户时出错: " + e.getMessage());
        }
    }
    
    private void removeRoleFromUser() {
        try {
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "角色", "分配")) {
                System.out.println("您没有权限从用户移除角色。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试从用户移除角色但没有权限");
                return;
            }
            
            System.out.print("请输入用户ID: ");
            Long userId = Long.parseLong(scanner.nextLine());
            
            System.out.print("请输入角色ID: ");
            Long roleId = Long.parseLong(scanner.nextLine());
            
            boolean result = rbacService.removeRoleFromUser(userId, roleId);
            if (result) {
                System.out.println("从用户移除角色成功。");
                LoggerUtil.info("用户 " + currentUser.getUsername() + " 从用户ID: " + userId + " 移除了角色ID: " + roleId);
            } else {
                System.out.println("从用户移除角色失败，请检查用户和角色是否存在。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试移除角色失败。用户ID: " + userId + ", 角色ID: " + roleId);
            }
        } catch (NumberFormatException e) {
            System.out.println("ID格式无效。");
            LoggerUtil.error("removeRoleFromUser中ID格式无效: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("从用户移除角色时出错: " + e.getMessage());
            LoggerUtil.error("从用户移除角色时出错: " + e.getMessage());
        }
    }
    
    private void grantPermissionToRole() {
        try {
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "权限", "授予")) {
                System.out.println("您没有权限授予角色权限。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试授予角色权限但没有权限");
                return;
            }
            
            System.out.print("请输入角色ID: ");
            Long roleId = Long.parseLong(scanner.nextLine());
            
            System.out.print("请输入权限ID: ");
            Long permissionId = Long.parseLong(scanner.nextLine());
            
            boolean result = rbacService.grantPermissionToRole(roleId, permissionId);
            if (result) {
                System.out.println("权限授予角色成功。");
                LoggerUtil.info("用户 " + currentUser.getUsername() + " 将权限ID: " + permissionId + " 授予角色ID: " + roleId);
            } else {
                System.out.println("权限授予角色失败，请检查角色和权限是否存在。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试授予权限失败。角色ID: " + roleId + ", 权限ID: " + permissionId);
            }
        } catch (NumberFormatException e) {
            System.out.println("ID格式无效。");
            LoggerUtil.error("grantPermissionToRole中ID格式无效: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("授予权限给角色时出错: " + e.getMessage());
            LoggerUtil.error("授予权限给角色时出错: " + e.getMessage());
        }
    }
    
    private void revokePermissionFromRole() {
        try {
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "权限", "授予")) {
                System.out.println("您没有权限从角色撤销权限。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试从角色撤销权限但没有权限");
                return;
            }
            
            System.out.print("请输入角色ID: ");
            Long roleId = Long.parseLong(scanner.nextLine());
            
            System.out.print("请输入权限ID: ");
            Long permissionId = Long.parseLong(scanner.nextLine());
            
            boolean result = rbacService.revokePermissionFromRole(roleId, permissionId);
            if (result) {
                System.out.println("从角色撤销权限成功。");
                LoggerUtil.info("用户 " + currentUser.getUsername() + " 从角色ID: " + roleId + " 撤销了权限ID: " + permissionId);
            } else {
                System.out.println("从角色撤销权限失败，请检查角色和权限是否存在。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试撤销权限失败。角色ID: " + roleId + ", 权限ID: " + permissionId);
            }
        } catch (NumberFormatException e) {
            System.out.println("ID格式无效。");
            LoggerUtil.error("revokePermissionFromRole中ID格式无效: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("从角色撤销权限时出错: " + e.getMessage());
            LoggerUtil.error("从角色撤销权限时出错: " + e.getMessage());
        }
    }
    
    private void viewRolePermissions() {
        try {
            System.out.print("请输入要查看权限的角色ID: ");
            Long roleId = Long.parseLong(scanner.nextLine());
            
            Optional<Role> roleOpt = rbacService.findRoleById(roleId);
            if (!roleOpt.isPresent()) {
                System.out.println("未找到角色。");
                return;
            }
            
            // Check permission
            if (!rbacService.checkPermission(currentUser.getId(), "角色", "查看")) {
                System.out.println("您没有权限查看角色权限。");
                LoggerUtil.warn("用户 " + currentUser.getUsername() + " 尝试查看角色权限但没有权限");
                return;
            }
            
            // Get role permissions
            List<Permission> rolePermissions = rbacService.getRolePermissions(roleId);
            
            System.out.println("\n--- 角色 \"" + roleOpt.get().getName() + "\" 的权限 ---");
            if (rolePermissions.isEmpty()) {
                System.out.println("该角色未分配任何权限。");
            } else {
                for (Permission permission : rolePermissions) {
                    System.out.println("ID: " + permission.getId() + ", 名称: " + permission.getName() + 
                                     ", 资源: " + permission.getResource() + ", 操作: " + permission.getAction() +
                                     ", 描述: " + permission.getDescription());
                }
            }
            
            LoggerUtil.info("用户 " + currentUser.getUsername() + " 查看了角色 \"" + roleOpt.get().getName() + "\" 的权限");
        } catch (NumberFormatException e) {
            System.out.println("角色ID格式无效。");
            LoggerUtil.error("viewRolePermissions中角色ID格式无效: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("获取角色权限时出错: " + e.getMessage());
            LoggerUtil.error("获取角色权限时出错: " + e.getMessage());
        }
    }
}
