# 🎉 项目已适配阿里云通义千问!

## 关键改动说明

您已成功将项目的AI功能从OpenAI切换到阿里云通义千问(DashScope)。以下是关键改动:

### 1. Maven依赖更新 (pom.xml)

```xml
<!-- 使用通义千问的Spring Boot Starter -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-spring-boot-starter</artifactId>
    <version>0.35.0</version>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-dashscope-spring-boot-starter</artifactId>
    <version>0.35.0</version>
</dependency>
```

### 2. 配置文件更新 (application.yaml)

```yaml
langchain4j:
  dashscope:
    chat-model:
      api-key: ${DASHSCOPE_API_KEY:sk-664256e37ba2498db97965cfa62178aa}
      model-name: qwen-max          # 使用最强模型
      temperature: 0.7               # 平衡创造性和准确性
      top-p: 0.8                     # 采样参数
      enable-search: true            # 启用搜索增强
```

### 3. 配置类简化 (LangChain4jConfig.java)

由于使用了Spring Boot Starter,配置类已简化,自动装配`ChatLanguageModel` Bean。

### 4. 业务代码无需修改

AIService.java中的代码完全不需要修改!
- `@Autowired ChatLanguageModel chatLanguageModel` 自动注入
- `chatLanguageModel.generate(prompt)` 调用方式完全相同

这正是LangChain4j的优势 - **统一的抽象接口,切换模型零代码改动**!

## 通义千问 vs OpenAI

### 优势

✅ **国内访问稳定** - 无需翻墙,响应速度快
✅ **中文能力强** - 专门针对中文场景优化
✅ **价格实惠** - qwen-max: ¥0.04/1K输入tokens, ¥0.12/1K输出tokens
✅ **搜索增强** - 支持联网获取最新信息
✅ **合规安全** - 符合国内数据安全要求
✅ **新用户优惠** - 通常有100万tokens免费额度

### 对比

| 特性 | 通义千问 qwen-max | OpenAI GPT-4 |
|------|-------------------|--------------|
| 中文能力 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| 英文能力 | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| 访问速度(国内) | ⭐⭐⭐⭐⭐ | ⭐⭐ |
| 价格 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| 搜索增强 | ✅ | ❌(需额外配置) |

## 快速开始

### 1. 获取API Key

访问 [阿里云百炼平台](https://bailian.console.aliyun.com/):
1. 登录/注册阿里云账号
2. 开通DashScope服务
3. 创建API Key
4. 复制Key(格式: sk-xxxxxx)

### 2. 配置API Key

```bash
# 方式1: 环境变量(推荐)
export DASHSCOPE_API_KEY=sk-你的Key

# 方式2: 直接修改application.yaml
# 替换配置中的api-key值
```

### 3. 启动测试

```bash
# 启动应用
./start.sh

# 或
mvn spring-boot:run

# 测试AI功能
curl -X POST http://localhost:8080/api/student/ai/consult \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"message":"我的成绩怎么样?"}'
```

## 文档更新

所有相关文档已更新:

1. ✅ **README.md** - 技术栈、配置说明、常见问题
2. ✅ **PROJECT_SUMMARY.md** - 项目总结
3. ✅ **LangChain4jConfig.java** - 配置类注释
4. ✅ **start.sh** - 启动脚本提示
5. ✅ **DASHSCOPE_CONFIG.md** - 详细配置指南(新增)

## 完整功能

AI功能完全可用:

### 学生AI助手
- 📊 分析个人成绩表现
- 📚 识别薄弱科目
- 💡 提供学习方法建议
- 🎯 规划未来发展方向

### 教师AI助手
- 📈 评估整体教学效果
- 📉 分析成绩分布合理性
- 👥 识别学困生群体
- 🔧 提供教学改进建议

## 费用控制

### 开发测试阶段
- 使用免费额度(100万tokens)
- 估算:每次咨询~850 tokens,可调用约1000次
- 足够完成项目开发和测试

### 生产环境
- 日均100次调用: ~¥6/天,~¥180/月
- 日均1000次调用: ~¥60/天,~¥1800/月
- 可根据实际需求选择qwen-plus降低成本

## 注意事项

1. ⚠️ **API Key安全**: 不要将API Key提交到Git仓库
2. ⚠️ **额度监控**: 定期查看阿里云控制台的用量
3. ⚠️ **异常处理**: AIService已有异常捕获,调用失败会返回友好提示
4. ⚠️ **并发控制**: 高并发时建议加入限流机制

## 测试建议

1. **基础测试**: 先用简单问题测试AI响应
2. **场景测试**: 测试学生和教师两种角色的AI功能
3. **边界测试**: 测试无成绩、成绩不全等边界情况
4. **性能测试**: 观察响应时间(通常1-3秒)

## 切换回OpenAI

如果需要切换回OpenAI,参考 `DASHSCOPE_CONFIG.md` 中的"如何切换回OpenAI"章节。

---

**🎊 恭喜!项目已成功适配阿里云通义千问,可以开始使用AI功能了!**

详细配置说明请查看: **DASHSCOPE_CONFIG.md**

如有问题,请参考README.md中的常见问题部分。
