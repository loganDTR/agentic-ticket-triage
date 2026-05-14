package it.zengfx.agentictickettriage.graph;

import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;

@Component
public class TicketTriageGraph {
    public final CompiledGraph<TicketTriageState> graph;

    public TicketTriageGraph(LlmClassifyTicketNode llmClassifyTicketNode) throws GraphStateException {
        this.graph = new StateGraph<>(
                TicketTriageState.SCHEMA,
                TicketTriageState::new
        )
                .addNode("classifyTicket", node_async(llmClassifyTicketNode))
                .addNode("decideRoute", node_async(new DecideRouteNode()))
                .addNode("billingAnswer", node_async(new BillingAnswerNode()))
                .addNode("technicalAnswer", node_async(new TechnicalAnswerNode()))
                .addNode("humanEscalationAnswer", node_async(new HumanEscalationAnswerNode()))
                .addEdge(START, "classifyTicket")
                .addEdge("classifyTicket", "decideRoute")
                .addConditionalEdges(
                        "decideRoute",
                        edge_async(this::routeToAnswerNode),
                        Map.of(
                                "billingAnswer", "billingAnswer",
                                "technicalAnswer", "technicalAnswer",
                                "humanEscalationAnswer", "humanEscalationAnswer"
                        )
                )
                .addEdge("billingAnswer", END)
                .addEdge("technicalAnswer", END)
                .addEdge("humanEscalationAnswer", END)
                .compile();
    }

    private String routeToAnswerNode(TicketTriageState state){
        return switch(state.route()){
            case "billingSupport" -> "billingAnswer";
            case "technicalSupport" -> "technicalAnswer";
            default -> "humanEscalationAnswer";
        };
    }

    public TicketTriageState run(String text){
        return graph.invoke(Map.of(TicketTriageState.TEXT, text))
                .orElseThrow(() -> new IllegalStateException("Ticket triage graph returned no final state"));
    }
}
