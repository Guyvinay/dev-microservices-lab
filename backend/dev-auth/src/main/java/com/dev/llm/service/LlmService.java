package com.dev.llm.service;

import com.dev.llm.dto.LlmChatRequest;
import com.dev.llm.dto.LlmChatResponse;

public interface LlmService {
    LlmChatResponse chat(LlmChatRequest request);
}
