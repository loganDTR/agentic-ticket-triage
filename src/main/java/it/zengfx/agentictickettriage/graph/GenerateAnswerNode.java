package it.zengfx.agentictickettriage.graph;

import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

public class GenerateAnswerNode implements NodeAction <TicketTriageState> {
    @Override
    public Map<String, Object> apply(TicketTriageState state) throws Exception {
        String answer = "Ticket classificato come "
                + state.category()
                + " e instradato verso "
                +  state.route();
        return Map.of(TicketTriageState.ANSWER, answer);
    }
}
