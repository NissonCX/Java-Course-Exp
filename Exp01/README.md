# 学生成绩管理系统（许多Bug未修复）

一个基于Java语言开发的命令行学生成绩管理系统，提供了完整的学生成绩管理解决方案。

## 目录
- [项目简介](#项目简介)
- [功能特性](#功能特性)
- [技术架构](#技术架构)
- [系统需求](#系统需求)
- [快速开始](#快速开始)
- [使用指南](#使用指南)
- [项目结构](#项目结构)
- [核心组件](#核心组件)
- [数据模型](#数据模型)
- [开发规范](#开发规范)
- [贡献者](#贡献者)
- [许可证](#许可证)

## 项目简介

学生成绩管理系统是一个基于Java的控制台应用程序，旨在提供一套完整的学生成绩管理解决方案。该系统能够自动生成模拟数据，包括学生、教师、课程、教学班和成绩等信息，并支持多种成绩查询和统计分析功能。

此项目作为Java企业级应用课程的实验项目，展示了面向对象编程、集合框架、流式API等Java核心技术的应用。

## 功能特性

- **数据初始化**: 自动生成大量模拟数据，包括学生、教师、课程、教学班和成绩记录
- **教学班成绩管理**: 查看特定教学班的学生成绩，支持按学号或成绩排序
- **学生个人成绩查询**: 根据学号或姓名查询学生的全部课程成绩
- **成绩统计分析**: 统计特定课程的成绩分布情况
- **综合排名**: 提供学生总成绩排名和各课程成绩排名
- **用户友好界面**: 基于命令行的交互式界面，操作简单直观

## 技术架构

- **编程语言**: Java 21
- **构建工具**: Apache Maven
- **依赖管理**: Lombok (简化Java代码)
- **架构模式**: 分层架构 (Model-View-Controller)

## 系统需求

- Java JDK 21 或更高版本
- Apache Maven 3.6 或更高版本
- 至少 512MB 可用内存

## 快速开始

1. 克隆项目到本地：
   ```
   git clone <repository-url>
   ```

2. 进入项目目录：
   ```
   cd java-exp01-nissoncx
   ```

3. 编译并打包项目：
   ```
   mvn clean package
   ```

4. 运行应用程序：
   ```
   java -cp target/java-exp01-nissoncx-1.0-SNAPSHOT.jar com.cqu.Application
   ```

## 使用指南

系统启动后会显示主菜单，用户可以通过输入数字选择相应功能：

```
========== 主菜单 ==========
1. 初始化数据
2. 查看教学班成绩
3. 查询学生所有科目成绩
4. 统计成绩分布
5. 学生总成绩排名
6. 课程成绩排名
0. 退出系统
============================
```

首次使用需要选择"1. 初始化数据"来生成模拟数据，之后即可使用其他功能。

## 项目结构

```
src/
├── main/
│   ├── java/com/cqu/
│   │   ├── controller/     # 控制器层，处理业务逻辑
│   │   ├── model/          # 数据模型层
│   │   ├── view/           # 视图层，控制台界面
│   │   └── Application.java # 应用程序入口
│   └── resources/          # 资源文件
└── test/                   # 测试代码
```

## 核心组件

### 控制器层 (Controller)

- [DataGenerator.java](file:///Users/nissoncx/code/git-study/src/main/java/com/cqu/controller/DataGenerator.java) - 负责生成各种模拟数据，包括学生、教师、课程、教学班和成绩
- [GradeManagementController.java](file:///Users/nissoncx/code/git-study/src/main/java/com/cqu/controller/GradeManagementController.java) - 核心业务逻辑控制器，处理所有成绩管理相关的操作

### 视图层 (View)

- [ConsoleView.java](file:///Users/nissoncx/code/git-study/src/main/java/com/cqu/view/ConsoleView.java) - 控制台用户界面，负责与用户的交互和数据显示

### 入口类

- [Application.java](file:///Users/nissoncx/code/git-study/src/main/java/com/cqu/Application.java) - 程序入口点

## 数据模型

系统包含以下核心数据模型：

1. **Person.java** - 人员基类，定义了人员的基本属性
2. **Student.java** - 学生实体，继承自Person类
3. **Teacher.java** - 教师实体，继承自Person类
4. **Course.java** - 课程实体，表示一门具体的课程
5. **Clazz.java** - 教学班实体，表示某门课程的一个具体教学班
6. **Grade.java** - 成绩实体，记录学生在某个教学班的各项成绩

## 开发规范

本项目遵循以下开发规范：

1. **代码风格**: 遵循Java编码规范，使用有意义的变量和方法命名
2. **注释规范**: 重要方法和复杂逻辑添加JavaDoc注释
3. **分层架构**: 严格遵循MVC架构模式，分离关注点
4. **异常处理**: 对可能出错的操作进行适当的异常处理
5. **集合操作**: 充分利用Java 8 Stream API进行数据处理

## 贡献者

项目作者: Nisson_CX

## 许可证

本项目仅供学习交流使用。