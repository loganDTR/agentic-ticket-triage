package it.zengfx.agentictickettriage.graph;

import it.zengfx.agentictickettriage.model.TicketCategory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Route.BILLING_SUPPORT;
import static org.assertj.core.api.Assertions.assertThat;

class TicketTriageStateTest {

    @Test
    void textReturnsStoredValue() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "hello world"));
        assertThat(state.text()).isEqualTo("hello world");
    }

    @Test
    void textReturnsEmptyStringWhenAbsent() {
        TicketTriageState state = new TicketTriageState(Map.of());
        assertThat(state.text()).isEqualTo("");
    }

    @Test
    void categoryReturnsStoredValue() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.CATEGORY, TicketCategory.BILLING));
        assertThat(state.category()).isEqualTo(TicketCategory.BILLING);
    }

    @Test
    void categoryReturnsGeneralWhenAbsent() {
        TicketTriageState state = new TicketTriageState(Map.of());
        assertThat(state.category()).isEqualTo(TicketCategory.GENERAL);
    }

    @Test
    void confidenceReturnsStoredValue() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.CONFIDENCE, 85));
        assertThat(state.confidence()).isEqualTo(85);
    }

    @Test
    void confidenceReturnsZeroWhenAbsent() {
        TicketTriageState state = new TicketTriageState(Map.of());
        assertThat(state.confidence()).isZero();
    }

    @Test
    void routeReturnsStoredValue() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.ROUTE, BILLING_SUPPORT));
        assertThat(state.route()).isEqualTo(BILLING_SUPPORT);
    }

    @Test
    void routeReturnsEmptyStringWhenAbsent() {
        TicketTriageState state = new TicketTriageState(Map.of());
        assertThat(state.route()).isEqualTo("");
    }

    @Test
    void answerReturnsStoredValue() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.ANSWER, "Some answer"));
        assertThat(state.answer()).isEqualTo("Some answer");
    }

    @Test
    void answerReturnsEmptyStringWhenAbsent() {
        TicketTriageState state = new TicketTriageState(Map.of());
        assertThat(state.answer()).isEqualTo("");
    }

    @Test
    void executionIdReturnsStoredValue() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.EXECUTION_ID, "exec-42"));
        assertThat(state.executionId()).isEqualTo("exec-42");
    }

    @Test
    void executionIdReturnsEmptyStringWhenAbsent() {
        TicketTriageState state = new TicketTriageState(Map.of());
        assertThat(state.executionId()).isEqualTo("");
    }

    @Test
    void executionTraceReturnsStoredList() {
        List<String> trace = new ArrayList<>(List.of("node1", "node2"));
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.EXECUTION_TRACE, trace));
        assertThat(state.executionTrace()).containsExactly("node1", "node2");
    }

    @Test
    void executionTraceReturnsEmptyListWhenAbsent() {
        TicketTriageState state = new TicketTriageState(Map.of());
        assertThat(state.executionTrace()).isEmpty();
    }

    @Test
    void traceWithAppendsNodeNameToExistingTrace() {
        List<String> existing = new ArrayList<>(List.of("node1", "node2"));
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.EXECUTION_TRACE, existing));
        List<String> result = state.traceWith("node3");
        assertThat(result).containsExactly("node1", "node2", "node3");
    }

    @Test
    void traceWithWorksWhenTraceIsEmpty() {
        TicketTriageState state = new TicketTriageState(Map.of());
        List<String> result = state.traceWith("firstNode");
        assertThat(result).containsExactly("firstNode");
    }

    @Test
    void traceWithDoesNotMutateOriginalTrace() {
        List<String> existing = new ArrayList<>(List.of("node1"));
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.EXECUTION_TRACE, existing));
        state.traceWith("node2");
        assertThat(state.executionTrace()).containsExactly("node1");
    }
}
