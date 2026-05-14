package it.zengfx.agentictickettriage.model;

public record TriageResponse(
        String originalText,
        TicketCategory category,
        String route,
        String answer
) {}
