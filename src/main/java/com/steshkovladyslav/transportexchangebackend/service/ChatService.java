package com.steshkovladyslav.transportexchangebackend.service;

import com.steshkovladyslav.transportexchangebackend.dto.EventType;
import com.steshkovladyslav.transportexchangebackend.dto.ObjectType;
import com.steshkovladyslav.transportexchangebackend.model.Chat;
import com.steshkovladyslav.transportexchangebackend.model.ChatMessage;
import com.steshkovladyslav.transportexchangebackend.model.LegalUser;
import com.steshkovladyslav.transportexchangebackend.model.User;
import com.steshkovladyslav.transportexchangebackend.repo.ChatMessageRepo;
import com.steshkovladyslav.transportexchangebackend.repo.ChatRepo;
import com.steshkovladyslav.transportexchangebackend.repo.LegalUserRepo;
import com.steshkovladyslav.transportexchangebackend.repo.UserRepo;
import com.steshkovladyslav.transportexchangebackend.util.WsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Service
public class ChatService {
    private final UserRepo userRepo;
    private final ChatRepo chatRepo;
    private final ChatMessageRepo chatMessageRepo;
    private final BiConsumer<EventType, ChatMessage> wsSender;

    @Autowired
    public ChatService(UserRepo userRepo, ChatRepo chatRepo, ChatMessageRepo chatMessageRepo, WsSender wsSender) {
        this.userRepo = userRepo;
        this.chatRepo = chatRepo;
        this.chatMessageRepo = chatMessageRepo;
        this.wsSender = wsSender.getSender(ObjectType.MESSAGE);
    }

    public ChatMessage addMessage(ChatMessage chatMessage, long idUser, long idUserFromCargo) {
        Chat chat = null;

        if (idUser != 0L && idUserFromCargo != 0L) {
            User user = userRepo.findById(idUser);
            chatMessage.setUser(user);

            User userFromCargo = userRepo.findById(idUserFromCargo);

            if (user.getChats() != null && userFromCargo.getChats() != null) {
                for (Chat c : user.getChats()) {
                    for (Chat cFromCargo : userFromCargo.getChats()) {
                        if (c.getId().equals(cFromCargo.getId())) {
                            chat = chatRepo.findById((long) c.getId());
                        }
                    }
                }
            }

            setChatUser(chatMessage, chat, user, userFromCargo);

            return saveAndSendMessage(chatMessage);
        }

        return null;
    }

    private void setChatUser(ChatMessage chatMessage, Chat chat, User user, User userFromCargo) {
        if (chat != null) {
            chatMessage.setChat(chat);
        } else {
            chat = new Chat();
            chatRepo.save(chat);

            chatMessage.setChat(chat);

            user.getChats().add(chat);
            userFromCargo.getChats().add(chat);

            userRepo.save(user);
            userRepo.save(userFromCargo);
        }
    }

    private ChatMessage saveAndSendMessage(ChatMessage chatMessage) {
        ChatMessage message = chatMessageRepo.save(chatMessage);
        wsSender.accept(EventType.CREATE, message);

        return message;
    }

    public Map<String, Object> getUsersOfChats(long id) {
        Map<String, Object> usersMap = new HashMap<>();
        List<User> users = new ArrayList<>();

        if (id != 0L) {
            User user = userRepo.findById(id);

            for (Chat chat : user.getChats()) {
                User companionUser = userRepo.getByIdChatAndUser(chat.getId(), user.getId());

                if (companionUser != null) {
                    users.add(companionUser);
                }
            }

            usersMap.put("users", users);
            usersMap.put("chats", user.getChats());

            return usersMap;
        }

        return null;
    }

    public Map<String, Object> getMessages(long idUser, long idUserCompanion, long idChat) {
        Map<String, Object> messagesMap = new HashMap<>();

        List<ChatMessage> messages;

        if (idUser != 0L && idUserCompanion != 0L) {
            User user = userRepo.findById(idUser);

            User userCompanion = userRepo.findById(idUserCompanion);

            messages = chatMessageRepo.findAllByChat_Id(idChat);

            messagesMap.put("user", user);
            messagesMap.put("userCompanion", userCompanion);
            messagesMap.put("messages", messages);

            return messagesMap;
        }

        return null;
    }
}
