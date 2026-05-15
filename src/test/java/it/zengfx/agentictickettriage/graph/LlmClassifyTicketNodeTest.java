package it.zengfx.agentictickettriage.graph;

import dev.langchain4j.model.chat.ChatModel;
import it.zengfx.agentictickettriage.agent.TicketClassifierAgent;
import it.zengfx.agentictickettriage.model.ClassificationResult;
import it.zengfx.agentictickettriage.model.TicketCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Trace.CLASSIFY_TICKET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LlmClassifyTicketNodeTest {

    private LlmClassifyTicketNode node;
    private TicketClassifierAgent mockAgent;

    @BeforeEach
    void setUp() throws Exception {
        ChatModel chatModel = mock(ChatModel.class);
        node = new LlmClassifyTicketNode(chatModel);
        mockAgent = mock(TicketClassifierAgent.class);
        Field agentField = LlmClassifyTicketNode.class.getDeclaredField("agent");
        agentField.setAccessible(true);
        agentField.set(node, mockAgent);
    }

    @Test
    void delegatesClassificationToAgentAndStoresCategory() {
        when(mockAgent.classify("Problema con fattura")).thenReturn(new ClassificationResult(TicketCategory.BILLING, 92));
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "Problema con fattura"));

        Map<String, Object> result = node.apply(state);

        assertThat(result.get(TicketTriageState.CATEGORY)).isEqualTo(TicketCategory.BILLING);
    }

    @Test
    void delegatesClassificationToAgentAndStoresConfidence() {
        when(mockAgent.classify("Problema con fattura")).thenReturn(new ClassificationResult(TicketCategory.BILLING, 92));
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "Problema con fattura"));

        Map<String, Object> result = node.apply(state);

        assertThat(result.get(TicketTriageState.CONFIDENCE)).isEqualTo(92);
    }

    @Test
    void addsClassifyTicketToExecutionTrace() {
        when(mockAgent.classify("Errore 500")).thenReturn(new ClassificationResult(TicketCategory.TECHNICAL, 88));
        List<String> existing = new ArrayList<>();
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.TEXT, "Errore 500",
                TicketTriageState.EXECUTION_TRACE, existing
        ));

        Map<String, Object> result = node.apply(state);

        @SuppressWarnings("unchecked")
        List<String> trace = (List<String>) result.get(TicketTriageState.EXECUTION_TRACE);
        assertThat(trace).contains(CLASSIFY_TICKET);
    }

    @Test
    void handlesLowConfidenceResult() {
        when(mockAgent.classify("non capisco")).thenReturn(new ClassificationResult(TicketCategory.GENERAL, 30));
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "non capisco"));

        Map<String, Object> result = node.apply(state);

        assertThat(result.get(TicketTriageState.CATEGORY)).isEqualTo(TicketCategory.GENERAL);
        assertThat(result.get(TicketTriageState.CONFIDENCE)).isEqualTo(30);
    }
}
