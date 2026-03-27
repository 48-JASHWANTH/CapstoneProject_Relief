package org.hartford.relief.ai;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private static final Logger log = Logger.getLogger(ChatController.class.getName());

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String conversationId = (request.getConversationId() == null || request.getConversationId().trim().isEmpty())
                ? UUID.randomUUID().toString()
                : request.getConversationId();

        try {
            String responseContent = chatService.chat(conversationId, request.getMessage());
            if (responseContent == null || responseContent.isBlank()) {
                return ResponseEntity.ok(new ChatResponse("I received your message but got an empty response. Please try again."));
            }
            return ResponseEntity.ok(new ChatResponse(responseContent));
        } catch (Exception e) {
            log.severe("Groq API error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChatResponse("AI service error: " + e.getMessage()));
        }
    }
}
