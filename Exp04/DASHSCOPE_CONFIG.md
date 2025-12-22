# 通义千问(DashScope) AI功能配置指南

本项目使用阿里云的通义千问大模型(qwen-max)提供AI智能功能。

## 为什么选择通义千问?

相比OpenAI GPT模型,通义千问有以下优势:

1. **国内访问稳定**: 无需翻墙,访问速度快
2. **中文理解能力强**: 针对中文场景优化,更适合教育场景
3. **价格实惠**: 相比OpenAI价格更低
4. **合规性**: 符合国内数据安全和隐私保护要求
5. **搜索增强**: 支持联网搜索,获取最新信息

## 配置步骤

### 1. 获取API Key

1. 访问 [阿里云百炼平台](https://bailian.console.aliyun.com/)
2. 登录阿里云账号(如果没有,需要先注册)
3. 开通DashScope服务
4. 进入"API-KEY管理"
5. 创建新的API Key
6. 复制API Key(格式类似: `sk-xxxxxxxxxxxxxx`)

### 2. 配置到项目

#### 方式一: 环境变量(推荐)

```bash
# Linux/Mac
export DASHSCOPE_API_KEY=sk-你的API-Key

# Windows
set DASHSCOPE_API_KEY=sk-你的API-Key
```

#### 方式二: 直接修改配置文件

编辑 `src/main/resources/application.yaml`:

```yaml
langchain4j:
  dashscope:
    chat-model:
      api-key: sk-你的API-Key  # 直接填写
      model-name: qwen-max
      temperature: 0.7
      top-p: 0.8
      enable-search: true
```

## 配置参数说明

### model-name (模型选择)

通义千问提供多个模型,根据需求选择:

- **qwen-max**: 最强模型,推理能力最好(默认)
- **qwen-plus**: 均衡模型,性能和成本兼顾
- **qwen-turbo**: 快速模型,响应速度快,成本低

建议使用 `qwen-max` 以获得最佳效果。

### temperature (创造性)

控制输出的随机性:
- 范围: 0.0 - 1.0
- 默认: 0.7
- 低值(0.2): 输出更确定、一致
- 高值(0.9): 输出更创造、多样

教育场景建议使用 0.7,既保证准确性又有一定灵活性。

### top-p (核采样)

控制输出的多样性:
- 范围: 0.0 - 1.0
- 默认: 0.8
- 与temperature配合使用
- 建议保持默认值

### enable-search (搜索增强)

是否启用联网搜索:
- `true`: 启用(默认),可以获取最新信息
- `false`: 禁用,仅使用模型知识

教育场景建议启用,以获取最新的学习资源和方法。

## 功能测试

### 学生AI助手测试

```bash
# 登录学生账号
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"2023100001","password":"123456"}'

# 获取学习建议
curl -X POST http://localhost:8080/api/student/ai/consult \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"message":"我的数据库成绩不理想,该如何提高?"}'
```

### 教师AI助手测试

```bash
# 登录教师账号
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"T1001","password":"123456"}'

# 获取教学分析
curl -X POST http://localhost:8080/api/teacher/ai/consult \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"teachingClassId":1,"message":"请分析班级整体学习情况"}'
```

## AI提示词设计

项目中的AI功能使用了精心设计的提示词:

### 学生助手提示词

```
你是一位经验丰富的学业导师。
根据以下学生的成绩情况,回答学生的问题并提供建议。

{成绩数据}

学生的问题: {用户问题}

请提供具体、实用的建议,包括:
1. 对学生当前学业表现的分析
2. 需要重点关注的科目
3. 学习方法建议
4. 未来发展方向建议

请用简洁、鼓励的语气回答,字数控制在300字以内。
```

### 教师助手提示词

```
你是一位教学分析专家。
根据以下教学班的成绩统计数据,回答教师的问题并提供教学建议。

{统计数据}

教师的问题: {用户问题}

请提供专业的分析和建议,包括:
1. 整体教学效果评价
2. 成绩分布合理性分析
3. 需要重点关注的学生群体
4. 教学改进建议

请用专业、客观的语气回答,字数控制在400字以内。
```

## 费用说明

通义千问采用按Token计费:

### qwen-max 定价
- 输入: ¥0.04 / 1K tokens
- 输出: ¥0.12 / 1K tokens

### 估算示例
一次典型的AI咨询:
- 学生成绩数据: ~500 tokens
- 用户问题: ~50 tokens
- AI回答: ~300 tokens
- 总计: ~850 tokens
- 费用: ~¥0.06

一天100次调用,费用约 ¥6。

### 新用户优惠
阿里云通常为新用户提供:
- 免费额度: 100万tokens
- 体验期: 3个月
- 足够本项目开发和测试使用

## 常见问题

### Q1: API Key无效

检查:
1. API Key是否正确复制(不要有多余空格)
2. 是否已开通DashScope服务
3. 账号是否有余额或免费额度

### Q2: 响应速度慢

可能原因:
1. 使用了qwen-max(最强但较慢),可以换qwen-plus
2. 启用了搜索增强,可以临时关闭测试
3. 网络问题,检查网络连接

### Q3: 返回内容不理想

优化建议:
1. 调整temperature参数(降低可提高准确性)
2. 优化提示词,提供更多上下文
3. 使用更强的模型(qwen-max)

### Q4: 如何切换回OpenAI

修改 `pom.xml`:
```xml
<!-- 移除 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-dashscope-spring-boot-starter</artifactId>
</dependency>

<!-- 添加 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
</dependency>
```

修改 `application.yaml`:
```yaml
langchain4j:
  openai:
    api-key: your-openai-key
    model-name: gpt-4
```

修改 `LangChain4jConfig.java`,参考原始OpenAI配置。

## 参考资源

- [阿里云百炼平台](https://bailian.console.aliyun.com/)
- [通义千问文档](https://help.aliyun.com/zh/dashscope/)
- [LangChain4j文档](https://docs.langchain4j.dev/)
- [DashScope定价](https://help.aliyun.com/zh/dashscope/developer-reference/tongyi-qianwen-metering-and-billing)

## 技术支持

如果遇到问题:
1. 查看项目README.md
2. 查看阿里云控制台的调用日志
3. 检查应用日志(控制台输出)
4. 联系阿里云技术支持

---

**提示**: 在开发测试阶段,建议先使用免费额度。正式部署前,评估实际调用量并合理控制成本。
