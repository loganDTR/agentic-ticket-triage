package it.zengfx.agentictickettriage.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class InvoiceToolTest {

    private InvoiceTool invoiceTool;

    @BeforeEach
    void setUp() {
        invoiceTool = new InvoiceTool();
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "12346", "12347", "12348", "12349", "123410"})
    void knownInvoicesAreReportedAsPaid(String invoiceId) {
        String result = invoiceTool.getInvoiceStatus(invoiceId);
        assertThat(result).isEqualTo("Invoice " + invoiceId + " is PAID");
    }

    @ParameterizedTest
    @ValueSource(strings = {"123411", "123412"})
    void knownInvoicesAreReportedAsOverdue(String invoiceId) {
        String result = invoiceTool.getInvoiceStatus(invoiceId);
        assertThat(result).isEqualTo("Invoice " + invoiceId + " is OVERDUE");
    }

    @Test
    void unknownInvoiceIdReturnsNotFoundMessage() {
        String result = invoiceTool.getInvoiceStatus("99999");
        assertThat(result).isEqualTo("Invoice 99999 was not found");
    }

    @Test
    void emptyInvoiceIdReturnsNotFoundMessage() {
        String result = invoiceTool.getInvoiceStatus("");
        assertThat(result).isEqualTo("Invoice  was not found");
    }
}
