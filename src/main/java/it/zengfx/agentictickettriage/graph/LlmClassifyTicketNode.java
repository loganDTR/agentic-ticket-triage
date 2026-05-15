package it.zengfx.agentictickettriage.graph;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import it.zengfx.agentictickettriage.agent.TicketClassifierAgent;
import it.zengfx.agentictickettriage.model.ClassificationResult;
import it.zengfx.agentictickettriage.model.TicketCategory;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

public class LlmClassifyTicketNode  implements NodeAction<TicketTriageState> {

    private final TicketClassifierAgent agent;

    public LlmClassifyTicketNode(ChatModel chatModel) {
        this.agent = AiServices.create(TicketClassifierAgent.class, chatModel);
    }

    @Override
    public Map<String, Object> apply(TicketTriageState state) {
        try{
            ClassificationResult result = agent.classify(state.text());
            return Map.of(
                    TicketTriageState.CATEGORY, result.category(),
                    TicketTriageState.CONFIDENCE, result.confidence(),
                    TicketTriageState.EXECUTION_TRACE, state.traceWith("classifyTicket")
            );
        }catch(Exception e){
            return Map.of(
                    TicketTriageState.CATEGORY, TicketCategory.ESCALATION,
                    TicketTriageState.CONFIDENCE, 0,
                    TicketTriageState.ROUTE, "humanEscalation",
                    TicketTriageState.ERROR, "Classification failed: " + e.getClass().getSimpleName(),
                    TicketTriageState.EXECUTION_TRACE, state.traceWith("classifyTicket:error")
            );
        }
    }
}
