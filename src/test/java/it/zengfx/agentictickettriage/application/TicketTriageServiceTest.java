package it.zengfx.agentictickettriage.application;

import it.zengfx.agentictickettriage.graph.TicketTriageGraph;
import it.zengfx.agentictickettriage.graph.TicketTriageState;
import it.zengfx.agentictickettriage.model.TicketCategory;
import it.zengfx.agentictickettriage.model.TriageRequest;
import it.zengfx.agentictickettriage.model.TriageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketTriageServiceTest {

    @Mock
    private TicketTriageGraph ticketTriageGraph;

    private TicketTriageService service;

    @BeforeEach
    void setUp() {
        service = new TicketTriageService(ticketTriageGraph);
    }

    @Test
    void triageMapsAllFieldsFromGraphState() {
        TicketTriageState finalState = new TicketTriageState(Map.of(
                TicketTriageState.EXECUTION_ID, "exec-123",
                TicketTriageState.TEXT, "Problema fattura",
                TicketTriageState.CATEGORY, TicketCategory.BILLING,
                TicketTriageState.CONFIDENCE, 90,
                TicketTriageState.ROUTE, "billingSupport",
                TicketTriageState.ANSWER, "La fattura è pagata.",
                TicketTriageState.EXECUTION_TRACE, new ArrayList<>(List.of("classifyTicket", "decideRoute", "billingAnswer"))
        ));
        when(ticketTriageGraph.run("Problema fattura")).thenReturn(finalState);

        TriageResponse response = service.triage(new TriageRequest("Problema fattura"));

        assertThat(response.executionId()).isEqualTo("exec-123");
        assertThat(response.originalText()).isEqualTo("Problema fattura");
        assertThat(response.category()).isEqualTo(TicketCategory.BILLING);
        assertThat(response.confidence()).isEqualTo(90);
        assertThat(response.route()).isEqualTo("billingSupport");
        assertThat(response.answer()).isEqualTo("La fattura è pagata.");
        assertThat(response.executionTrace()).containsExactly("classifyTicket", "decideRoute", "billingAnswer");
    }

    @Test
    void triageDelegatesToGraphWithRequestText() {
        TicketTriageState finalState = new TicketTriageState(Map.of(
                TicketTriageState.TEXT, "Errore di accesso",
                TicketTriageState.CATEGORY, TicketCategory.TECHNICAL,
                TicketTriageState.CONFIDENCE, 85,
                TicketTriageState.ROUTE, "technicalSupport",
                TicketTriageState.ANSWER, "Supporto tecnico."
        ));
        when(ticketTriageGraph.run("Errore di accesso")).thenReturn(finalState);

        TriageResponse response = service.triage(new TriageRequest("Errore di accesso"));

        assertThat(response.category()).isEqualTo(TicketCategory.TECHNICAL);
        assertThat(response.route()).isEqualTo("technicalSupport");
    }

    @Test
    void triageHandlesHumanEscalationRoute() {
        TicketTriageState finalState = new TicketTriageState(Map.of(
                TicketTriageState.TEXT, "Help",
                TicketTriageState.CATEGORY, TicketCategory.GENERAL,
                TicketTriageState.CONFIDENCE, 20,
                TicketTriageState.ROUTE, "humanEscalation",
                TicketTriageState.ANSWER, "Escalo a operatore."
        ));
        when(ticketTriageGraph.run("Help")).thenReturn(finalState);

        TriageResponse response = service.triage(new TriageRequest("Help"));

        assertThat(response.route()).isEqualTo("humanEscalation");
        assertThat(response.confidence()).isEqualTo(20);
    }
}
