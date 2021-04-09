package com.steshkovladyslav.transportexchangebackend.controller;

import com.steshkovladyslav.transportexchangebackend.model.ChatMessage;
import com.steshkovladyslav.transportexchangebackend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(value = "/add-message")
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public ChatMessage addMessage(
            @RequestBody ChatMessage chatMessage,
            @RequestParam long idUser,
            @RequestParam long idUserCompanion,
            @RequestParam String role,
            @RequestParam String roleUserFromCargo
    ) {
        return chatService.addMessage(chatMessage, role, roleUserFromCargo, idUser, idUserCompanion);
    }

    @GetMapping("/get-users-of-chats/{id}")
    public Map<String, Object> getUsersOfChats(
            @PathVariable long id,
            @RequestParam String role
    ) {
        return chatService.getUsersOfChats(id, role);
    }

    @GetMapping("/get-messages/{id}")
    public Map<String, Object> getMessages(
            @PathVariable("id") long idUser,
            @RequestParam long idUserCompanion,
            @RequestParam long idChat,
            @RequestParam String role,
            @RequestParam String roleUserCompanion
    ) {
        return chatService.getMessages(idUser, idUserCompanion, role, roleUserCompanion, idChat);
    }
}
