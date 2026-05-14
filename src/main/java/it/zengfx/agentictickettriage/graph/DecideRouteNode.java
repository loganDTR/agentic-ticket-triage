package it.zengfx.agentictickettriage.graph;

import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

public class DecideRouteNode implements NodeAction<TicketTriageState> {

    private static final int MIN_CONFIDENCE = 60;

    @Override
    public Map<String, Object> apply(TicketTriageState state) throws Exception {
        if (state.conficence() < MIN_CONFIDENCE) {
            return Map.of(TicketTriageState.ROUTE, "humanEscalation");
        }

        String route = switch (state.category()) {
            case BILLING -> "billingSupport";
            case TECHNICAL -> "technicalSupport";
            case GENERAL, ESCALATION -> "humanEscalation";
        };
        return Map.of(TicketTriageState.ROUTE, route);
    }
}
