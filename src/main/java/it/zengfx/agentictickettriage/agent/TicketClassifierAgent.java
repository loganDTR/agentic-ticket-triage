package it.zengfx.agentictickettriage.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import it.zengfx.agentictickettriage.model.ClassificationResult;

public interface TicketClassifierAgent {
    @SystemMessage("""
            You are a ticket classification system.

            Classify the ticket into one of these categories:
            - TECHNICAL
            - BILLING
            - GENERAL
            - ESCALATION

            Respond only with the structured result.            
            """)
    ClassificationResult classify(@UserMessage String userMessage);
}
