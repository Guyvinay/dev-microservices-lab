package com.dev.llm.service.openai;

import com.dev.llm.dto.LlmChatRequest;
import com.dev.llm.dto.LlmChatResponse;
import com.dev.llm.service.LlmService;

public class OpenAiLlmService implements LlmService {

    @Override
    public LlmChatResponse chat(LlmChatRequest request) {
//        String response = chatClient.call(request.userMessage());
//        return new LlmChatResponse(null);
        return null;
    }
}
