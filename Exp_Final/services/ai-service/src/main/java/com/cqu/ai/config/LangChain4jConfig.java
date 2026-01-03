package com.cqu.ai.config;

import dev.langchain4j.model.dashscope.QwenChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LangChain4j 配置
 */
@Configuration
public class LangChain4jConfig {

    @Value("${langchain4j.dashscope.api-key:sk-123456}")
    private String apiKey;

    @Bean
    public QwenChatModel qwenChatModel() {
        return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-turbo") // 使用通义千问模型
                .temperature(0.7f) // 创造性 (使用 float)
                .maxTokens(1000) // 最大响应长度
                .build();
    }
}