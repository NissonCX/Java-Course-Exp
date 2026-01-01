package com.cqu.exp04.config;

import dev.langchain4j.model.dashscope.QwenChatModel;
import dev.langchain4j.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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

    @Value("${langchain4j.dashscope.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.dashscope.chat-model.model-name}")
    private String modelName;

    @Value("${langchain4j.dashscope.chat-model.temperature:0.7}")
    private Double temperature;

    @Value("${langchain4j.dashscope.chat-model.top-p:0.8}")
    private Double topP;

    // Spring Boot Starter 会自动配置 ChatLanguageModel Bean
    // 但需要手动配置 StreamingChatLanguageModel

    @Bean
    public StreamingChatLanguageModel streamingChatLanguageModel() {
        return QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature.floatValue())
                .topP((double) topP.floatValue())
                .enableSearch(true)
                .build();
    }
}
