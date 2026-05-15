package it.zengfx.agentictickettriage.graph;

import it.zengfx.agentictickettriage.model.TicketCategory;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TicketTriageState extends AgentState {
    public static final String TEXT = "text";
    public static final String CATEGORY = "category";
    public static final String CONFIDENCE = "confidence";
    public static final String ROUTE = "route";
    public static final String ANSWER = "answer";
    public static final String EXECUTION_TRACE = "executionTrace";
    public static final String EXECUTION_ID = "executionId";
    public static final String ERROR = "error";

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            TEXT, Channels.base(() -> ""),
            CATEGORY, Channels.base(() -> TicketCategory.GENERAL),
            CONFIDENCE, Channels.base(() -> ""),
            ROUTE, Channels.base(() -> ""),
            ANSWER, Channels.base(() -> ""),
            EXECUTION_TRACE, Channels.base(ArrayList::new),
            EXECUTION_ID, Channels.base(() -> ""),
            ERROR, Channels.base(() -> "")
    );

    public TicketTriageState(Map<String, Object> initData) {
        super(initData);
    }

    public String text(){
        return this.<String>value(TEXT).orElse("");
    }

    public TicketCategory category(){
        return this.<TicketCategory>value(CATEGORY).orElse(TicketCategory.GENERAL);
    }

    public int confidence() {
        return this.<Integer>value(CONFIDENCE).orElse(0);
    }

    public String route(){
        return this.<String>value(ROUTE).orElse("");
    }

    public String answer(){
        return this.<String>value(ANSWER).orElse("");
    }

    @SuppressWarnings("unchecked")
    public List<String> executionTrace() {
        return this.<List<String>>value(EXECUTION_TRACE).orElseGet(ArrayList::new);
    }

    public List<String> traceWith(String nodeName) {
        List<String> trace = new ArrayList<>(executionTrace());
        trace.add(nodeName);
        return trace;
    }

    public String executionId() {
        return this.<String>value(EXECUTION_ID).orElse("");
    }

    public String error() {
        return this.<String>value(ERROR).orElse("");
    }

    public boolean hasError() {
        return !error().isBlank();
    }
}
