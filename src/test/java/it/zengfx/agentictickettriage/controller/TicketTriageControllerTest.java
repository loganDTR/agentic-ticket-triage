package it.zengfx.agentictickettriage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.zengfx.agentictickettriage.application.TicketTriageService;
import it.zengfx.agentictickettriage.model.TicketCategory;
import it.zengfx.agentictickettriage.model.TriageRequest;
import it.zengfx.agentictickettriage.model.TriageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.BILLING_ANSWER;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.CLASSIFY_TICKET;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.DECIDE_ROUTE;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.TECHNICAL_ANSWER;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Route.BILLING_SUPPORT;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Route.TECHNICAL_SUPPORT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TicketTriageControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TicketTriageService ticketTriageService;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TicketTriageController(ticketTriageService))
                .setValidator(validator)
                .build();
    }

    @Test
    void triageReturns200WithResponseBodyForValidRequest() throws Exception {
        TriageResponse mockResponse = new TriageResponse(
                "exec-1",
                "Problema fattura 12345",
                TicketCategory.BILLING,
                92,
                BILLING_SUPPORT,
                "La fattura 12345 risulta pagata.",
                "",
                List.of(CLASSIFY_TICKET, DECIDE_ROUTE, BILLING_ANSWER)
        );
        when(ticketTriageService.triage(any(TriageRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/tickets/triage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TriageRequest("Problema fattura 12345"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executionId").value("exec-1"))
                .andExpect(jsonPath("$.originalText").value("Problema fattura 12345"))
                .andExpect(jsonPath("$.category").value("BILLING"))
                .andExpect(jsonPath("$.confidence").value(92))
                .andExpect(jsonPath("$.route").value(BILLING_SUPPORT))
                .andExpect(jsonPath("$.answer").value("La fattura 12345 risulta pagata."))
                .andExpect(jsonPath("$.executionTrace[0]").value(CLASSIFY_TICKET));
    }

    @Test
    void triageReturns400WhenTextIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/tickets/triage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void triageReturns400WhenTextIsNull() throws Exception {
        mockMvc.perform(post("/api/v1/tickets/triage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void triageReturns400WhenBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/tickets/triage")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void triageDelegatesToServiceWithCorrectText() throws Exception {
        TriageResponse mockResponse = new TriageResponse(
                "exec-2",
                "Errore di login",
                TicketCategory.TECHNICAL,
                88,
                TECHNICAL_SUPPORT,
                "Supporto tecnico.",
                "",
                List.of(CLASSIFY_TICKET, DECIDE_ROUTE, TECHNICAL_ANSWER)
        );
        when(ticketTriageService.triage(any(TriageRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/tickets/triage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TriageRequest("Errore di login"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("TECHNICAL"))
                                .andExpect(jsonPath("$.route").value(TECHNICAL_SUPPORT));
    }
}
