package it.zengfx.agentictickettriage.graph;

import it.zengfx.agentictickettriage.model.TicketCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ClassifyTicketNodeTest {

    private ClassifyTicketNode node;

    @BeforeEach
    void setUp() {
        node = new ClassifyTicketNode();
    }

    @Test
    void classifiesAsBillingWhenTextContainsPagamento() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "Problema con il pagamento"));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.CATEGORY)).isEqualTo(TicketCategory.BILLING);
    }

    @Test
    void classifiesAsBillingWhenTextContainsFattura() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "Non ho ricevuto la fattura"));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.CATEGORY)).isEqualTo(TicketCategory.BILLING);
    }

    @Test
    void classifiesAsBillingWhenTextContainsCarta() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "Carta di credito rifiutata"));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.CATEGORY)).isEqualTo(TicketCategory.BILLING);
    }

    @Test
    void classifiesAsTechnicalWhenTextContainsErrore() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "Errore 500 sul server"));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.CATEGORY)).isEqualTo(TicketCategory.TECHNICAL);
    }

    @Test
    void classifiesAsTechnicalWhenTextContainsLogin() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "Non riesco a fare login"));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.CATEGORY)).isEqualTo(TicketCategory.TECHNICAL);
    }

    @Test
    void classifiesAsTechnicalWhenTextContainsTimeout() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "La richiesta va in timeout"));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.CATEGORY)).isEqualTo(TicketCategory.TECHNICAL);
    }

    @Test
    void classifiesAsGeneralWhenNoKeywordMatches() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "Vorrei informazioni generali"));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.CATEGORY)).isEqualTo(TicketCategory.GENERAL);
    }

    @Test
    void classificationIsCaseInsensitiveForBilling() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "PAGAMENTO fallito"));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.CATEGORY)).isEqualTo(TicketCategory.BILLING);
    }

    @Test
    void classificationIsCaseInsensitiveForTechnical() {
        TicketTriageState state = new TicketTriageState(Map.of(TicketTriageState.TEXT, "ERRORE durante l'accesso"));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.CATEGORY)).isEqualTo(TicketCategory.TECHNICAL);
    }

    @Test
    void billingKeywordTakesPrecedenceOverTechnicalKeyword() {
        TicketTriageState state = new TicketTriageState(
                Map.of(TicketTriageState.TEXT, "errore nella fattura"));
        Map<String, Object> result = node.apply(state);
        assertThat(result.get(TicketTriageState.CATEGORY)).isEqualTo(TicketCategory.BILLING);
    }
}
