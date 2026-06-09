package com.intifix.modules.chat.websocket;

import com.intifix.modules.chat.dto.SendMessageRequest;
import com.intifix.modules.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private final ChatService chatService;

    public ChatWebSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat.send")
    public void send(@Payload SendMessageRequest request) {
        chatService.sendMessage(request);
    }
}
