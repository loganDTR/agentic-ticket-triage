package it.zengfx.agentictickettriage.graph;

import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Route.BILLING_SUPPORT;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Route.HUMAN_ESCALATION;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Route.TECHNICAL_SUPPORT;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Trace.DECIDE_ROUTE;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Trace.DECIDE_ROUTE_ERROR_FALLBACK;

public class DecideRouteNode implements NodeAction<TicketTriageState> {

    private static final int MIN_CONFIDENCE = 60;

    @Override
    public Map<String, Object> apply(TicketTriageState state) throws Exception {

        if(state.hasError()){
            return Map.of(
                    TicketTriageState.ROUTE, HUMAN_ESCALATION,
                    TicketTriageState.EXECUTION_TRACE, state.traceWith(DECIDE_ROUTE_ERROR_FALLBACK)
            );
        }

        if (state.confidence() < MIN_CONFIDENCE) {
            return Map.of(TicketTriageState.ROUTE, HUMAN_ESCALATION,
                    TicketTriageState.EXECUTION_TRACE, state.traceWith(DECIDE_ROUTE)
                    );
        }

        String route = switch (state.category()) {
            case BILLING -> BILLING_SUPPORT;
            case TECHNICAL -> TECHNICAL_SUPPORT;
            case GENERAL, ESCALATION -> HUMAN_ESCALATION;
        };
        return Map.of(TicketTriageState.ROUTE, route,
                TicketTriageState.EXECUTION_TRACE, state.traceWith(DECIDE_ROUTE)
                );
    }
}
