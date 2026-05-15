package it.zengfx.agentictickettriage.application;

import it.zengfx.agentictickettriage.graph.TicketTriageGraph;
import it.zengfx.agentictickettriage.graph.TicketTriageState;
import it.zengfx.agentictickettriage.model.TicketCategory;
import it.zengfx.agentictickettriage.model.TriageRequest;
import it.zengfx.agentictickettriage.model.TriageResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class TicketTriageService {

    private final TicketTriageGraph ticketTriageGraph;

    public TriageResponse triage(TriageRequest  request) {
        TicketTriageState finalState = ticketTriageGraph.run(request.text());
        return new TriageResponse(
          finalState.executionId(),
          finalState.text(),
          finalState.category(),
          finalState.confidence(),
          finalState.route(),
          finalState.answer(),
          finalState.error(),
          finalState.executionTrace()
        );
    }
}
