package it.zengfx.agentictickettriage.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class InvoiceTool {
    @Tool("Return the payment status of an invoice by invoice id")
    public String getInvoiceStatus(
            @P("Invoice id, for example 12345")
            String invoiceId
    ){
        return switch(invoiceId) {
            case "12345" -> "Invoice 12345 is PAID";
            case "12346" -> "Invoice 12346 is PAID";
            case "12347" -> "Invoice 12347 is PAID";
            case "12348" -> "Invoice 12348 is PAID";
            case "12349" -> "Invoice 12349 is PAID";
            case "123410" -> "Invoice 123410 is PAID";
            case "123411" -> "Invoice 123411 is OVERDUE";
            case "123412" -> "Invoice 123412 is OVERDUE";
            default -> "Invoice "+ invoiceId +" was not found";
        };
    }
}
