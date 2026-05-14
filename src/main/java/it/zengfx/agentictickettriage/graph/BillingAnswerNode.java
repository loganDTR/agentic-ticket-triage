package it.zengfx.agentictickettriage.graph;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import it.zengfx.agentictickettriage.agent.BillingAgent;
import it.zengfx.agentictickettriage.tool.InvoiceTool;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

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
        String answer = agent.answer(state.text());
        return Map.of(TicketTriageState.ANSWER,
                answer
                );
    }
}
