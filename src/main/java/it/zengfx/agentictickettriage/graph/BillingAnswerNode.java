package it.zengfx.agentictickettriage.graph;

import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

public class BillingAnswerNode implements NodeAction<TicketTriageState> {

    @Override
    public Map<String, Object> apply(TicketTriageState state) throws Exception {
        return Map.of(TicketTriageState.ANSWER,
                "Sembra un problema di pagamento/fatturazione. Ti indirizzo al team di billing"
                );
    }
}
