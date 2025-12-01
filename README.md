# Java课程实验

本仓库包含重庆大学计算机学院**Java企业级应用开发**课程的实验作业。

## 课程信息

- **课程名称**：Java企业级应用开发
- **授课教师**：曾令秋
- **学期**：2025-2026学年 大三上学期

## 实验内容

### 实验一：命令行学生成绩管理系统

基于Java开发的命令行学生成绩管理系统，展示了以下Java核心技术：
- 面向对象编程原则
- 集合框架的使用
- Java 8 Stream API进行数据处理
- MVC架构模式

#### 主要功能
- 自动生成模拟数据（学生、教师、课程、班级和成绩）
- 班级成绩管理与排序功能
- 按学号或姓名查询学生成绩
- 成绩统计与分布分析
- 学生排名系统

[实验一详情](Exp01/README.md)

### 实验二：基于角色的访问控制(RBAC)权限管理系统

完整的基于角色的访问控制系统，实现权限管理功能：
- 用户、角色和权限管理
- 安全认证机制
- 细粒度访问控制
- 审计日志功能
- MySQL数据库集成

#### 主要功能
- 完整的RBAC模型实现，支持多对多关系
- 带尝试次数限制的安全登录
- 基于角色的功能访问控制
- 操作审计日志
- 防止自我删除功能，增强系统安全性

[实验二详情](Exp02/SystemDesign.md)

## 项目结构

```
Java-Course-Exp/
├── Exp01/                 # 学生成绩管理系统
│   ├── src/               # 源代码
│   ├── README.md          # 详细文档
│   └── pom.xml            # Maven配置文件
├── Exp02/                 # RBAC权限管理系统
│   ├── src/               # 源代码
│   ├── SystemDesign.md    # 系统设计文档
│   ├── ExperimentReport.md# 实验报告
│   └── pom.xml            # Maven配置文件
└── README.md              # 本文件
```

## 环境要求

- Java JDK 21或更高版本
- Apache Maven 3.6或更高版本
- MySQL数据库（实验二需要）

## 快速开始

1. 克隆仓库：
   ```bash
   git clone https://github.com/your-username/Java-Course-Exp.git
   ```

2. 进入特定实验目录：
   ```bash
   cd Java-Course-Exp/Exp01  # 或 Exp02
   ```

3. 编译和打包项目：
   ```bash
   mvn clean package
   ```

4. 运行应用程序：
   ```bash
   java -cp target/[jar-file-name].jar [main-class-name]
   ```

有关详细说明，请参阅每个实验的独立README文件。

## 注意事项

1. 本代码仅供学习参考，请勿直接抄袭
2. 代码可能存在不足或错误，欢迎指正
3. 使用前请确保理解代码逻辑
4. 使用此材料作为参考时请遵守学术诚信政策

## 许可证

本项目仅供学习交流使用。