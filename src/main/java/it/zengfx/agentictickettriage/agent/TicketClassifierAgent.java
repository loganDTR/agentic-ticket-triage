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
            Also return a confidence score from 0 to 100.
    
            Use low confidence when the ticket is ambiguous, too short, unclear,
            or could belong to multiple categories.
            
            Respond only with the structured result.            
            """)
    ClassificationResult classify(@UserMessage String userMessage);
}
