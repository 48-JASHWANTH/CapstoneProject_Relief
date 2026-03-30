package org.hartford.relief.ai;

import org.hartford.relief.repository.UserRepository;
import org.hartford.relief.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private static final Logger log = Logger.getLogger(ChatController.class.getName());

    private final ChatService chatService;
    private final UserRepository userRepository;

    public ChatController(ChatService chatService, UserRepository userRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String conversationId = (request.getConversationId() == null || request.getConversationId().trim().isEmpty())
                ? UUID.randomUUID().toString()
                : request.getConversationId();

        // Resolve userId and role from SecurityContext (set by JwtFilter)
        Long   resolvedUserId   = null;
        String resolvedUserRole = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                resolvedUserId = user.getId();
                if (user.getRole() != null) {
                    // Role name is stored as e.g. "ROLE_CUSTOMER" — strip the prefix
                    String rawRole = user.getRole().getName();
                    resolvedUserRole = rawRole.startsWith("ROLE_") ? rawRole.substring(5) : rawRole;
                }
            }
        }

        // If the frontend explicitly passed values (fallback / override), prefer SecurityContext
        // but keep frontend values as last resort when not authenticated
        if (resolvedUserId == null && request.getUserId() != null) {
            resolvedUserId   = request.getUserId();
            resolvedUserRole = request.getUserRole();
        }

        try {
            String responseContent = chatService.chat(conversationId, request.getMessage(), resolvedUserId, resolvedUserRole);
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
