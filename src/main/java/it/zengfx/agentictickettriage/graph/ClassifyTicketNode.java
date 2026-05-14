package it.zengfx.agentictickettriage.graph;

import it.zengfx.agentictickettriage.model.TicketCategory;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

public class ClassifyTicketNode implements NodeAction<TicketTriageState> {
    @Override
    public Map<String, Object> apply(TicketTriageState state){
        String text = state.text().toLowerCase();
        TicketCategory category;

        if (text.contains("pagamento") || text.contains("fattura") || text.contains("carta")) {
            category = TicketCategory.BILLING;
        } else if (text.contains("errore") || text.contains("login") || text.contains("timeout")) {
            category = TicketCategory.TECHNICAL;
        } else {
            category = TicketCategory.GENERAL;
        }

        return Map.of(TicketTriageState.CATEGORY, category);
    }
}
