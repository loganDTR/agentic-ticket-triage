package it.zengfx.agentictickettriage.graph;

import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

public class HumanEscalationAnswerNode implements NodeAction<TicketTriageState> {
    @Override
    public Map<String, Object> apply(TicketTriageState state) {
        return Map.of(
                TicketTriageState.ANSWER,
                "Non riesco a classificare il ticket con sufficiente precisione. Escalo a un operatore umano.",
                TicketTriageState.EXECUTION_TRACE, state.traceWith("humanEscalationAnswer")
        );
    }
}
