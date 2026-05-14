package it.zengfx.agentictickettriage.application;

import it.zengfx.agentictickettriage.model.TicketCategory;
import it.zengfx.agentictickettriage.model.TriageRequest;
import it.zengfx.agentictickettriage.model.TriageResponse;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class TicketTriageService {

    public TriageResponse triage(TriageRequest  request) {
        String text = request.text().toLowerCase();

        TicketCategory category;
        String route;
        String answer;

        if (text.contains("error") || text.contains("bug") || text.contains("issue")) {
            category = TicketCategory.TECHNICAL;
            route = "Route to technical support team";
            answer = "We have identified this as a technical issue. Our technical support team will assist you shortly.";
        } else if (text.contains("invoice") || text.contains("payment") || text.contains("billing")) {
            category = TicketCategory.BILLING;
            route = "Route to billing department";
            answer = "This appears to be a billing issue. Our billing department will review your case and get back to you.";
        } else if (text.contains("question") || text.contains("inquiry") || text.contains("help")) {
            category = TicketCategory.GENERAL;
            route = "Route to general support team";
            answer = "Your inquiry has been categorized as general. Our support team will assist you with your question.";
        } else {
            category = TicketCategory.ESCALATION;
            route = "Route to escalation team";
            answer = "We are unable to categorize your ticket based on the provided information. It has been escalated for further review.";
        }

        return new TriageResponse(
                request.text(),
                category,
                route,
                answer
        );
    }
}
