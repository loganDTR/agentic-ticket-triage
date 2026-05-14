package it.zengfx.agentictickettriage.graph;

import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

public class DecideRouteNode implements NodeAction<TicketTriageState> {

    @Override
    public Map<String, Object> apply(TicketTriageState state) throws Exception {
        String route = switch (state.category()) {
            case BILLING -> "billingSupport";
            case TECHNICAL -> "technicalSupport";
            case GENERAL, ESCALATION -> "humanEscalation";
        };
        return Map.of(TicketTriageState.ROUTE, route);
    }
}
