package it.zengfx.agentictickettriage.model;

public record TriageResponse(
        String originalText,
        TicketCategory category,
        int conficence,
        String route,
        String answer
) {}
