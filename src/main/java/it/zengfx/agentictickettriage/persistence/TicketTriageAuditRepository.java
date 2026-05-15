package it.zengfx.agentictickettriage.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTriageAuditRepository extends JpaRepository<TicketTriageAuditEntity, String> {
}
