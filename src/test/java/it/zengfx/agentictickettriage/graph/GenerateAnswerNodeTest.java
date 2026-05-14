package it.zengfx.agentictickettriage.graph;

import it.zengfx.agentictickettriage.model.TicketCategory;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GenerateAnswerNodeTest {

    private final GenerateAnswerNode node = new GenerateAnswerNode();

    @Test
    void generatesAnswerContainingCategoryAndRoute() throws Exception {
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.CATEGORY, TicketCategory.BILLING,
                TicketTriageState.ROUTE, "billingSupport"
        ));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.ANSWER).toString())
                .contains("BILLING")
                .contains("billingSupport");
    }

    @Test
    void generatesAnswerForTechnicalRoute() throws Exception {
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.CATEGORY, TicketCategory.TECHNICAL,
                TicketTriageState.ROUTE, "technicalSupport"
        ));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.ANSWER).toString())
                .contains("TECHNICAL")
                .contains("technicalSupport");
    }

    @Test
    void generatesAnswerForEscalationRoute() throws Exception {
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.CATEGORY, TicketCategory.ESCALATION,
                TicketTriageState.ROUTE, "humanEscalation"
        ));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.ANSWER).toString())
                .contains("ESCALATION")
                .contains("humanEscalation");
    }
}
