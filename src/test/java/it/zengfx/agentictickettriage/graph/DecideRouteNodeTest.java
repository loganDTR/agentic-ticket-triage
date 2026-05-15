package it.zengfx.agentictickettriage.graph;

import it.zengfx.agentictickettriage.model.TicketCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.CLASSIFY_TICKET;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Trace.DECIDE_ROUTE;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Route.BILLING_SUPPORT;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Route.HUMAN_ESCALATION;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Route.TECHNICAL_SUPPORT;
import static org.assertj.core.api.Assertions.assertThat;

class DecideRouteNodeTest {

    private DecideRouteNode node;

    @BeforeEach
    void setUp() {
        node = new DecideRouteNode();
    }

    @Test
    void routesToHumanEscalationWhenConfidenceBelowThreshold() throws Exception {
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.CONFIDENCE, 59,
                TicketTriageState.CATEGORY, TicketCategory.BILLING
        ));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.ROUTE)).isEqualTo(HUMAN_ESCALATION);
    }

    @Test
    void routesToHumanEscalationWhenConfidenceIsZero() throws Exception {
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.CONFIDENCE, 0,
                TicketTriageState.CATEGORY, TicketCategory.TECHNICAL
        ));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.ROUTE)).isEqualTo(HUMAN_ESCALATION);
    }

    @Test
    void routesToBillingSupportForBillingCategoryAtThreshold() throws Exception {
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.CONFIDENCE, 60,
                TicketTriageState.CATEGORY, TicketCategory.BILLING
        ));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.ROUTE)).isEqualTo(BILLING_SUPPORT);
    }

    @Test
    void routesToBillingSupportForBillingCategoryAboveThreshold() throws Exception {
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.CONFIDENCE, 90,
                TicketTriageState.CATEGORY, TicketCategory.BILLING
        ));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.ROUTE)).isEqualTo(BILLING_SUPPORT);
    }

    @Test
    void routesToTechnicalSupportForTechnicalCategory() throws Exception {
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.CONFIDENCE, 80,
                TicketTriageState.CATEGORY, TicketCategory.TECHNICAL
        ));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.ROUTE)).isEqualTo(TECHNICAL_SUPPORT);
    }

    @Test
    void routesToHumanEscalationForGeneralCategory() throws Exception {
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.CONFIDENCE, 75,
                TicketTriageState.CATEGORY, TicketCategory.GENERAL
        ));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.ROUTE)).isEqualTo(HUMAN_ESCALATION);
    }

    @Test
    void routesToHumanEscalationForEscalationCategory() throws Exception {
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.CONFIDENCE, 70,
                TicketTriageState.CATEGORY, TicketCategory.ESCALATION
        ));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.ROUTE)).isEqualTo(HUMAN_ESCALATION);
    }

    @Test
    void addsDecideRouteToExecutionTrace() throws Exception {
        List<String> existing = new ArrayList<>(List.of(CLASSIFY_TICKET));
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.CONFIDENCE, 80,
                TicketTriageState.CATEGORY, TicketCategory.BILLING,
                TicketTriageState.EXECUTION_TRACE, existing
        ));
        Map<String, Object> result = node.apply(state);
        @SuppressWarnings("unchecked")
        List<String> trace = (List<String>) result.get(TicketTriageState.EXECUTION_TRACE);
        assertThat(trace).containsExactly(CLASSIFY_TICKET, DECIDE_ROUTE);
    }

    @Test
    void addsDecideRouteToTraceAlsoWhenRoutingToHumanEscalationByLowConfidence() throws Exception {
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.CONFIDENCE, 30,
                TicketTriageState.CATEGORY, TicketCategory.BILLING
        ));
        Map<String, Object> result = node.apply(state);
        @SuppressWarnings("unchecked")
        List<String> trace = (List<String>) result.get(TicketTriageState.EXECUTION_TRACE);
        assertThat(trace).contains(DECIDE_ROUTE);
    }
}
