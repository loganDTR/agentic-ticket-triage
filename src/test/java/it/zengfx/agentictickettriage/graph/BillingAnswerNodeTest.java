package it.zengfx.agentictickettriage.graph;

import dev.langchain4j.model.chat.ChatModel;
import it.zengfx.agentictickettriage.agent.BillingAgent;
import it.zengfx.agentictickettriage.tool.InvoiceTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.CLASSIFY_TICKET;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.DECIDE_ROUTE;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Trace.BILLING_ANSWER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BillingAnswerNodeTest {

    private BillingAnswerNode node;
    private BillingAgent mockAgent;

    @BeforeEach
    void setUp() throws Exception {
        ChatModel chatModel = mock(ChatModel.class);
        InvoiceTool invoiceTool = mock(InvoiceTool.class);
        node = new BillingAnswerNode(chatModel, invoiceTool);
        mockAgent = mock(BillingAgent.class);
        Field agentField = BillingAnswerNode.class.getDeclaredField("agent");
        agentField.setAccessible(true);
        agentField.set(node, mockAgent);
    }

    @Test
    void delegatesAnsweringToAgentAndStoresAnswer() throws Exception {
        when(mockAgent.answer("Stato fattura 12345")).thenReturn("La fattura 12345 risulta PAGATA.");
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "Stato fattura 12345"));

        Map<String, Object> result = node.apply(state);

        assertThat(result.get(TicketTriageState.ANSWER)).isEqualTo("La fattura 12345 risulta PAGATA.");
    }

    @Test
    void addsBillingAnswerToExecutionTrace() throws Exception {
        when(mockAgent.answer("Stato fattura 12345")).thenReturn("La fattura 12345 risulta PAGATA.");
        List<String> existing = new ArrayList<>(List.of(CLASSIFY_TICKET, DECIDE_ROUTE));
        TicketTriageState state = new TicketTriageState(Map.of(
                TicketTriageState.TEXT, "Stato fattura 12345",
                TicketTriageState.EXECUTION_TRACE, existing
        ));

        Map<String, Object> result = node.apply(state);

        @SuppressWarnings("unchecked")
        List<String> trace = (List<String>) result.get(TicketTriageState.EXECUTION_TRACE);
        assertThat(trace).containsExactly(CLASSIFY_TICKET, DECIDE_ROUTE, BILLING_ANSWER);
    }

    @Test
    void addsToTraceWhenPreviousTraceIsEmpty() throws Exception {
        when(mockAgent.answer("info fattura")).thenReturn("Fornisci il numero fattura.");
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "info fattura"));

        Map<String, Object> result = node.apply(state);

        @SuppressWarnings("unchecked")
        List<String> trace = (List<String>) result.get(TicketTriageState.EXECUTION_TRACE);
        assertThat(trace).containsExactly(BILLING_ANSWER);
    }
}
