package it.zengfx.agentictickettriage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "langchain4j.open-ai.chat-model")
public record OpenAIChatModelProperties(
        String apiKey,
        String modelName,
        String baseUrl,
        Double temperature
) {}
