package it.zengfx.agentictickettriage.graph;

import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Component
public class TicketTriageGraph {
    public final CompiledGraph<TicketTriageState> graph;

    public TicketTriageGraph() throws GraphStateException {
        this.graph = new StateGraph<>(
                TicketTriageState.SCHEMA,
                TicketTriageState::new
        )
                .addNode("classifyTicket", node_async(new ClassifyTicketNode()))
                .addNode("decideRoute", node_async(new DecideRouteNode()))
                .addNode("generateAnswer", node_async(new GenerateAnswerNode()))
                .addEdge(START, "classifyTicket")
                .addEdge("classifyTicket", "decideRoute")
                .addEdge("decideRoute", "generateAnswer")
                .addEdge("generateAnswer", END)
                .compile();
    }

    public TicketTriageState run(String text){
        return graph.invoke(Map.of(TicketTriageState.TEXT, text))
                .orElseThrow(() -> new IllegalStateException("Ticket triage graph returned no final state"));
    }
}
