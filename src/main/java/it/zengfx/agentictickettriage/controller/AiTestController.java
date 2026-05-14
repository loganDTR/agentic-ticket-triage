package it.zengfx.agentictickettriage.controller;

import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai-test")
@RequiredArgsConstructor
public class AiTestController {

    private final OpenAiChatModel openAiChatModel;

    @GetMapping("/ping")
    public String ping() {
        return openAiChatModel.chat("Rispondi OK");
    }
}
