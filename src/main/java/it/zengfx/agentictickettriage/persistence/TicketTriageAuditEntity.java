package it.zengfx.agentictickettriage.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "ticket_triage_audit")
@AllArgsConstructor
@Getter
public class TicketTriageAuditEntity {
    @Id
    private String executionId;

    @Column(nullable = false, length = 4000)
    private String originalText;

    private String category;

    private int confidence;

    private String route;

    @Column(length = 4000)
    private String answer;

    @Column(length = 1000)
    private String error;

    @Column(length = 4000)
    private String executionTrace;

    private Instant createdAt;

    protected TicketTriageAuditEntity() {
    }
}
