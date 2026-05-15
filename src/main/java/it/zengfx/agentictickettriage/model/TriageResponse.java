package it.zengfx.agentictickettriage.model;

import java.util.List;

public record TriageResponse(
        String executionId,
        String originalText,
        TicketCategory category,
        int confidence,
        String route,
        String answer,
        String error,
        List<String> executionTrace
) {}
