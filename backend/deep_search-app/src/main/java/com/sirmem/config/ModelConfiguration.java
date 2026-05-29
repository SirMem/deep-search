package com.sirmem.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelConfiguration {


    @Bean
    public ChatModel getModel() {
        return OpenAiChatModel.builder()
                .apiKey("sk-")
                .build();
    }
}
