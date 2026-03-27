package org.hartford.relief.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("You are a helpful customer support chatbot for 'Relief', a Disaster Insurance Management System. " +
                        "Be polite, empathetic, and concise. Answer questions about the insurance policies available (Flood, Earthquake, Cyclone, Hurricane). " +
                        "Guide customers through the claim filing process if they ask. Do not invent information about policies that doesn't exist.")
                .build();
    }

    public String chat(String conversationId, String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
