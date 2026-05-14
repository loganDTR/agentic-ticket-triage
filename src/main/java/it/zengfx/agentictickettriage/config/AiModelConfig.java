package it.zengfx.agentictickettriage.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        OpenAIChatModelProperties.class
})
public class AiModelConfig {

    @Bean
    public OpenAiChatModel openAiChatModel(OpenAIChatModelProperties properties) {
        return OpenAiChatModel.builder()
                .apiKey(properties.apiKey())
                .modelName(properties.modelName())
                .baseUrl(properties.baseUrl())
                .temperature(properties.temperature())
                .build();
    }
}
