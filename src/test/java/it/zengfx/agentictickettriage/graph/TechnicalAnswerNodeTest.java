package it.zengfx.agentictickettriage.graph;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TechnicalAnswerNodeTest {

    private final TechnicalAnswerNode node = new TechnicalAnswerNode();

    @Test
    void returnsExpectedItalianAnswer() throws Exception {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "problema di login"));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.ANSWER))
                .isEqualTo("Sembra un problema tecnico. Ti indirizzo al team di supporto tecnico");
    }

    @Test
    void addsTechnicalAnswerToExecutionTrace() throws Exception {
        List<String> existing = new ArrayList<>(List.of("classifyTicket", "decideRoute"));
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.EXECUTION_TRACE, existing
        ));
        Map<String, Object> result = node.apply(state);
        @SuppressWarnings("unchecked")
        List<String> trace = (List<String>) result.get(TicketTriageState.EXECUTION_TRACE);
        assertThat(trace).containsExactly("classifyTicket", "decideRoute", "technicalAnswer");
    }

    @Test
    void addsToTraceWhenPreviousTraceIsEmpty() throws Exception {
        TicketTriageState state = new TicketTriageState(Map.of());
        Map<String, Object> result = node.apply(state);
        @SuppressWarnings("unchecked")
        List<String> trace = (List<String>) result.get(TicketTriageState.EXECUTION_TRACE);
        assertThat(trace).containsExactly("technicalAnswer");
    }
}
