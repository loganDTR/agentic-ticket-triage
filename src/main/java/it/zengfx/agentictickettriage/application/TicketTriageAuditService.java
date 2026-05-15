package it.zengfx.agentictickettriage.application;

import it.zengfx.agentictickettriage.graph.TicketTriageState;
import it.zengfx.agentictickettriage.persistence.TicketTriageAuditEntity;
import it.zengfx.agentictickettriage.persistence.TicketTriageAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketTriageAuditService {
    private final TicketTriageAuditRepository auditRepository;

    public void save(TicketTriageState state){
        String trace = String.join(" -> ", state.executionTrace());

        TicketTriageAuditEntity entity = new TicketTriageAuditEntity(
                state.executionId(),
                state.text(),
                state.category().name(),
                state.confidence(),
                state.route(),
                state.answer(),
                state.error(),
                trace,
                java.time.Instant.now()
        );
        auditRepository.save(entity);
    }
}
