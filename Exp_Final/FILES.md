# 📁 项目文件说明

## 核心文件（7个）

### 📚 文档（4个）
1. **README.md** - 项目主文档，快速上手指南
2. **CLAUDE.md** - 给 Claude Code 的项目开发指引
3. **NACOS-GUIDE.md** - Nacos 服务发现完整指南
4. **实验报告.md** - 实验报告文档

### 🔧 脚本（4个）
1. **start-nacos.sh** - 启动 Nacos 服务器（本地）
2. **stop-nacos.sh** - 停止 Nacos 服务器
3. **start-all-phase2.sh** - 启动所有微服务
4. **stop-all-phase2.sh** - 停止所有微服务

---

## 项目结构

```
Exp_Final/
├── 📄 核心文件（8个，见上文）
├── 📁 libs/                    # 共享库模块
│   ├── common/                 # 通用工具类、DTO、VO
│   └── security-common/        # JWT 安全组件
├── 📁 services/                # 微服务模块（9个）
│   ├── gateway/                # API 网关（8080端口）
│   ├── auth-service/           # 认证服务（8081端口）
│   ├── student-service/        # 学生服务（8083端口）
│   ├── teacher-service/        # 教师服务（8084端口）
│   ├── course-service/         # 课程服务（8085端口）
│   ├── score-service/          # 成绩服务（8086端口）
│   ├── admin-service/          # 管理员服务（8087端口）
│   ├── ai-service/             # AI 服务（8088端口）
│   └── core-service/           # 核心服务（8082端口，静态资源）
└── 📁 src/                     # 遗留单体代码（仅供参考）
```

---

## 快速开始

```bash
# 1. 启动 Nacos
./start-nacos.sh

# 2. 启动所有微服务
./start-all-phase2.sh

# 3. 访问系统
open http://localhost:8080/index.html

# 4. 访问 Nacos 控制台
open http://localhost:8848/nacos
# 用户名/密码: nacos/nacos
```

---

## 说明

- ✅ 所有冗余和临时文件已清理
- ✅ 项目保持最精简状态（7个核心文件）
- ✅ 微服务架构完整（9个服务 + Nacos 服务发现）
- ✅ 所有服务已配置 OpenFeign 跨服务调用
- ✅ 网关统一路由，支持负载均衡
- ✅ Nacos本地安装在 ~/nacos 目录
