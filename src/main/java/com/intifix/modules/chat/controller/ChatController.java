package com.intifix.modules.chat.controller;

import com.intifix.modules.chat.dto.ConversationDto;
import com.intifix.modules.chat.dto.CreateConversationRequest;
import com.intifix.modules.chat.dto.MessageDto;
import com.intifix.modules.chat.dto.SendMessageRequest;
import com.intifix.modules.chat.service.ChatService;
import com.intifix.shared.api.ApiResponse;
import com.intifix.shared.dto.PageRequestDto;
import com.intifix.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/conversations")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ConversationDto> createConversation(@Valid @RequestBody CreateConversationRequest req) {
        return ApiResponse.ok(chatService.createConversation(req));
    }

    @PostMapping("/messages")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<MessageDto> sendMessage(@Valid @RequestBody SendMessageRequest req) {
        return ApiResponse.ok(chatService.sendMessage(req));
    }

    @GetMapping("/conversations/{conversacionId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PageResponse<MessageDto>> listMessages(
            @PathVariable String conversacionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.ok(chatService.listMessages(conversacionId, new PageRequestDto(page, size)));
    }

    @PostMapping("/messages/{mensajeId}/read")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> markRead(@PathVariable String mensajeId) {
        chatService.markRead(mensajeId);
        return ApiResponse.ok(null);
    }
}
