package it.zengfx.agentictickettriage.graph;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        List<String> existing = new ArrayList<>(List.of("classifyTicket", "decideRoute"));
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.EXECUTION_TRACE, existing
        ));
        Map<String, Object> result = node.apply(state);
        @SuppressWarnings("unchecked")
        List<String> trace = (List<String>) result.get(TicketTriageState.EXECUTION_TRACE);
        assertThat(trace).containsExactly("classifyTicket", "decideRoute", "humanEscalationAnswer");
    }

    @Test
    void addsToTraceWhenPreviousTraceIsEmpty() {
        TicketTriageState state = new TicketTriageState(Map.of());
        Map<String, Object> result = node.apply(state);
        @SuppressWarnings("unchecked")
        List<String> trace = (List<String>) result.get(TicketTriageState.EXECUTION_TRACE);
        assertThat(trace).containsExactly("humanEscalationAnswer");
    }
}
