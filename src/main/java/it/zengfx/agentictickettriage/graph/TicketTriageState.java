package it.zengfx.agentictickettriage.graph;

import it.zengfx.agentictickettriage.model.TicketCategory;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.Map;

public class TicketTriageState extends AgentState {
    public static final String TEXT = "text";
    public static final String CATEGORY = "category";
    public static final String CONFICENCE = "conficence";
    public static final String ROUTE = "route";
    public static final String ANSWER = "answer";

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            TEXT, Channels.base(() -> ""),
            CATEGORY, Channels.base(() -> TicketCategory.GENERAL),
            CONFICENCE, Channels.base(() -> ""),
            ROUTE, Channels.base(() -> ""),
            ANSWER, Channels.base(() -> "")
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

    public int conficence() {
        return this.<Integer>value(CONFICENCE).orElse(0);
    }

    public String route(){
        return this.<String>value(ROUTE).orElse("");
    }

    public String answer(){
        return this.<String>value(ANSWER).orElse("");
    }
}
