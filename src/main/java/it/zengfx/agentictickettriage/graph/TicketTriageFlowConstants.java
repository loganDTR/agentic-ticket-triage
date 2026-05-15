package it.zengfx.agentictickettriage.graph;

/**
 * Centralized constants for graph node ids, routes and execution trace labels.
 */
public final class TicketTriageFlowConstants {

    private TicketTriageFlowConstants() {
    }

    public static final class NodeId {
        public static final String CLASSIFY_TICKET = "classifyTicket";
        public static final String DECIDE_ROUTE = "decideRoute";
        public static final String BILLING_ANSWER = "billingAnswer";
        public static final String TECHNICAL_ANSWER = "technicalAnswer";
        public static final String HUMAN_ESCALATION_ANSWER = "humanEscalationAnswer";

        private NodeId() {
        }
    }

    public static final class Route {
        public static final String BILLING_SUPPORT = "billingSupport";
        public static final String TECHNICAL_SUPPORT = "technicalSupport";
        public static final String HUMAN_ESCALATION = "humanEscalation";

        private Route() {
        }
    }

    public static final class Trace {
        public static final String CLASSIFY_TICKET = NodeId.CLASSIFY_TICKET;
        public static final String CLASSIFY_TICKET_ERROR = "classifyTicket:error";
        public static final String DECIDE_ROUTE = NodeId.DECIDE_ROUTE;
        public static final String DECIDE_ROUTE_ERROR_FALLBACK = "decideRoute:errorFallback";
        public static final String BILLING_ANSWER = NodeId.BILLING_ANSWER;
        public static final String BILLING_ANSWER_ERROR = "billingAnswer:error";
        public static final String TECHNICAL_ANSWER = NodeId.TECHNICAL_ANSWER;
        public static final String HUMAN_ESCALATION_ANSWER = NodeId.HUMAN_ESCALATION_ANSWER;

        private Trace() {
        }
    }
}

