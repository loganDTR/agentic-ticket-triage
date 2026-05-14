package it.zengfx.agentictickettriage.model;

import java.util.List;

public record TriageResponse(
        String originalText,
        TicketCategory category,
        int conficence,
        String route,
        String answer,
        List<String> executionTrace
) {}
