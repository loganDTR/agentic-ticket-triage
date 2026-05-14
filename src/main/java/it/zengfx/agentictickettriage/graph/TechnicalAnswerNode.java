package it.zengfx.agentictickettriage.graph;

import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

public class TechnicalAnswerNode implements NodeAction<TicketTriageState> {

    @Override
    public Map<String, Object> apply(TicketTriageState state) throws Exception {
        return Map.of(TicketTriageState.ANSWER,
                "Sembra un problema tecnico. Ti indirizzo al team di supporto tecnico",
                TicketTriageState.EXECUTION_TRACE, state.traceWith("technicalAnswer")
        );
    }
}
