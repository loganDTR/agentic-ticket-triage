package it.zengfx.agentictickettriage.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface BillingAgent {
    @SystemMessage("""
            You are a billing support assistant.
            
            If the user asks about an invoice status and provides an invoice id,
            use the available invoice status tool.
            
            If no invoice id is provided, ask the user to provide it.
            
            Answer in Italian.
            """)
    String answer(@UserMessage String userMessage);
}
