package it.zengfx.agentictickettriage.graph;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import it.zengfx.agentictickettriage.agent.BillingAgent;
import it.zengfx.agentictickettriage.tool.InvoiceTool;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Route.HUMAN_ESCALATION;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Trace.BILLING_ANSWER;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Trace.BILLING_ANSWER_ERROR;

public class BillingAnswerNode implements NodeAction<TicketTriageState> {
    private final BillingAgent agent;

    public BillingAnswerNode(ChatModel chatModel, InvoiceTool invoiceTool){
        this.agent = AiServices.builder(BillingAgent.class)
                .chatModel(chatModel)
                .tools(invoiceTool)
                .build();
    }

    @Override
    public Map<String, Object> apply(TicketTriageState state) throws Exception {
        try{
            String answer = agent.answer(state.text());
            return Map.of(TicketTriageState.ANSWER, answer,
                    TicketTriageState.EXECUTION_TRACE, state.traceWith(BILLING_ANSWER)
            );
        }catch (Exception e){
            return Map.of(
                    TicketTriageState.ANSWER, "Non riesco a recuperare le informazioni di fatturazione in questo momento. Escalo a un operatore umano.",
                    TicketTriageState.ROUTE, HUMAN_ESCALATION,
                    TicketTriageState.ERROR, "Billing answer failed: " + e.getClass().getSimpleName(),
                    TicketTriageState.EXECUTION_TRACE, state.traceWith(BILLING_ANSWER_ERROR)
            );
        }
    }
}
