package it.zengfx.agentictickettriage.model;

import jakarta.validation.constraints.NotBlank;

public record TriageRequest (
        @NotBlank(message = "Ticket message must not be blank")
        String text
){

}
