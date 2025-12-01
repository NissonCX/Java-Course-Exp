-- RBAC系统示例数据

-- 插入示例用户
INSERT INTO users (username, password, email) VALUES 
('管理员', 'admin123', 'admin@example.com'),
('经理', 'manager123', 'manager@example.com'),
('员工', 'employee123', 'employee@example.com');

-- 插入示例角色
INSERT INTO roles (name, description) VALUES 
('管理员', '系统管理员，拥有完全访问权限'),
('经理', '部门经理，拥有部门级访问权限'),
('员工', '普通员工，拥有有限访问权限');

-- 插入示例权限
INSERT INTO permissions (name, description, resource, action) VALUES 
('用户创建', '创建新用户', '用户', '创建'),
('用户查看', '查看用户信息', '用户', '查看'),
('用户更新', '更新用户信息', '用户', '更新'),
('用户删除', '删除用户', '用户', '删除'),
('角色分配', '为用户分配角色', '角色', '分配'),
('权限授予', '为角色授予权限', '权限', '授予'),
('报表查看', '查看报表', '报表', '查看'),
('部门管理', '管理部门资源', '部门', '管理');

-- 插入示例资源
INSERT INTO resources (name, description) VALUES 
('用户', '用户管理模块'),
('角色', '角色管理模块'),
('权限', '权限管理模块'),
('报表', '报表模块'),
('部门', '部门管理模块');

-- 为用户分配角色
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1), -- 管理员用户拥有管理员角色
(2, 2), -- 经理用户拥有经理角色
(3, 3); -- 员工用户拥有员工角色

-- 为角色分配权限
-- 管理员角色拥有所有权限
INSERT INTO role_permissions (role_id, permission_id) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8);

-- 经理角色拥有有限权限
INSERT INTO role_permissions (role_id, permission_id) VALUES 
(2, 2), (2, 3), (2, 7), (2, 8);

-- 员工角色拥有最少权限
INSERT INTO role_permissions (role_id, permission_id) VALUES 
(3, 2), (3, 7);