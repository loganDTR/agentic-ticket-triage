package it.zengfx.agentictickettriage.graph;

import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.BILLING_ANSWER;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.CLASSIFY_TICKET;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.DECIDE_ROUTE;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.HUMAN_ESCALATION_ANSWER;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.NodeId.TECHNICAL_ANSWER;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Route.BILLING_SUPPORT;
import static it.zengfx.agentictickettriage.graph.TicketTriageFlowConstants.Route.TECHNICAL_SUPPORT;
import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Component
public class TicketTriageGraph {
    public final CompiledGraph<TicketTriageState> graph;

    public TicketTriageGraph(LlmClassifyTicketNode llmClassifyTicketNode,
                             BillingAnswerNode billingAnswerNode
                             ) throws GraphStateException {
        this.graph = new StateGraph<>(
                TicketTriageState.SCHEMA,
                TicketTriageState::new
        )
                .addNode(CLASSIFY_TICKET, node_async(llmClassifyTicketNode))
                .addNode(DECIDE_ROUTE, node_async(new DecideRouteNode()))
                .addNode(BILLING_ANSWER, node_async(billingAnswerNode))
                .addNode(TECHNICAL_ANSWER, node_async(new TechnicalAnswerNode()))
                .addNode(HUMAN_ESCALATION_ANSWER, node_async(new HumanEscalationAnswerNode()))
                .addEdge(START, CLASSIFY_TICKET)
                .addEdge(CLASSIFY_TICKET, DECIDE_ROUTE)
                .addConditionalEdges(
                        DECIDE_ROUTE,
                        edge_async(this::routeToAnswerNode),
                        Map.of(
                                BILLING_ANSWER, BILLING_ANSWER,
                                TECHNICAL_ANSWER, TECHNICAL_ANSWER,
                                HUMAN_ESCALATION_ANSWER, HUMAN_ESCALATION_ANSWER
                        )
                )
                .addEdge(BILLING_ANSWER, END)
                .addEdge(TECHNICAL_ANSWER, END)
                .addEdge(HUMAN_ESCALATION_ANSWER, END)
                .compile();
    }

    private String routeToAnswerNode(TicketTriageState state){
        return switch(state.route()){
            case BILLING_SUPPORT -> BILLING_ANSWER;
            case TECHNICAL_SUPPORT -> TECHNICAL_ANSWER;
            default -> HUMAN_ESCALATION_ANSWER;
        };
    }

    public TicketTriageState run(String text){
        return graph.invoke(Map.of(
                TicketTriageState.EXECUTION_ID, UUID.randomUUID().toString(),
                TicketTriageState.TEXT, text
                ))
                .orElseThrow(() -> new IllegalStateException("Ticket triage graph returned no final state"));
    }
}
