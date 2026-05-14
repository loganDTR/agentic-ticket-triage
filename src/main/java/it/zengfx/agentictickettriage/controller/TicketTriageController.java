package it.zengfx.agentictickettriage.controller;

import it.zengfx.agentictickettriage.application.TicketTriageService;
import it.zengfx.agentictickettriage.model.TriageRequest;
import it.zengfx.agentictickettriage.model.TriageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketTriageController {
    private final TicketTriageService ticketTriageService;

    @PostMapping("/triage")
    public TriageResponse triage(@Valid @RequestBody TriageRequest request) {
        return ticketTriageService.triage(request);
    }
}
