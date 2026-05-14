package it.zengfx.agentictickettriage.config;

import it.zengfx.agentictickettriage.graph.LlmClassifyTicketNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.model.chat.ChatModel;

@Configuration
public class GraphNodeConfig {
    @Bean
    public LlmClassifyTicketNode llmClassifyTicketNode(ChatModel chatModel) {
        return new LlmClassifyTicketNode(chatModel);
    }
}
