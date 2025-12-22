package com.cqu.exp04.config;

import org.springframework.context.annotation.Configuration;

/**
 * LangChain4j配置
 * 使用阿里云通义千问(DashScope)模型
 *
 * 配置通过 langchain4j-dashscope-spring-boot-starter 自动完成
 * 相关配置在 application.yaml 中:
 * - langchain4j.dashscope.chat-model.api-key
 * - langchain4j.dashscope.chat-model.model-name (qwen-max)
 * - langchain4j.dashscope.chat-model.temperature
 * - langchain4j.dashscope.chat-model.top-p
 * - langchain4j.dashscope.chat-model.enable-search
 */
@Configuration
public class LangChain4jConfig {
    // Spring Boot Starter 会自动配置 ChatLanguageModel Bean
    // 无需手动配置,直接在 AIService 中 @Autowired 使用即可
}
