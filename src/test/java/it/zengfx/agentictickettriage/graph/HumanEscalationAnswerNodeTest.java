package it.zengfx.agentictickettriage.graph;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.CLASSIFY_TICKET;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.DECIDE_ROUTE;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Trace.HUMAN_ESCALATION_ANSWER;
import static org.assertj.core.api.Assertions.assertThat;

class HumanEscalationAnswerNodeTest {

    private final HumanEscalationAnswerNode node = new HumanEscalationAnswerNode();

    @Test
    void returnsExpectedItalianAnswer() {
        TicketTriageState state = new TicketTriageState(Map.of());
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.ANSWER))
                .isEqualTo("Non riesco a classificare il ticket con sufficiente precisione. Escalo a un operatore umano.");
    }

    @Test
    void addsHumanEscalationAnswerToExecutionTrace() {
        List<String> existing = new ArrayList<>(List.of(CLASSIFY_TICKET, DECIDE_ROUTE));
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.EXECUTION_TRACE, existing
        ));
        Map<String, Object> result = node.apply(state);
        @SuppressWarnings("unchecked")
        List<String> trace = (List<String>) result.get(TicketTriageState.EXECUTION_TRACE);
        assertThat(trace).containsExactly(CLASSIFY_TICKET, DECIDE_ROUTE, HUMAN_ESCALATION_ANSWER);
    }

    @Test
    void addsToTraceWhenPreviousTraceIsEmpty() {
        TicketTriageState state = new TicketTriageState(Map.of());
        Map<String, Object> result = node.apply(state);
        @SuppressWarnings("unchecked")
        List<String> trace = (List<String>) result.get(TicketTriageState.EXECUTION_TRACE);
        assertThat(trace).containsExactly(HUMAN_ESCALATION_ANSWER);
    }
}
