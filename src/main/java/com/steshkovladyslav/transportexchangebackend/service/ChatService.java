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
    private final LegalUserRepo legalUserRepo;
    private final ChatRepo chatRepo;
    private final ChatMessageRepo chatMessageRepo;
    private final BiConsumer<EventType, ChatMessage> wsSender;

    @Autowired
    public ChatService(UserRepo userRepo, LegalUserRepo legalUserRepo, ChatRepo chatRepo,
                       ChatMessageRepo chatMessageRepo, WsSender wsSender) {
        this.userRepo = userRepo;
        this.legalUserRepo = legalUserRepo;
        this.chatRepo = chatRepo;
        this.chatMessageRepo = chatMessageRepo;
        this.wsSender = wsSender.getSender(ObjectType.MESSAGE);
    }

    public ChatMessage addMessage(ChatMessage chatMessage, String role, String roleUserFromCargo, long idUser,
                                  long idUserFromCargo) {
        Chat chat = null;

        if (role.equals("ROLE_USER")) {
            User user = userRepo.findById(idUser);
            chatMessage.setUser(user);

            // If User write User
            if (roleUserFromCargo.equals("ROLE_USER")) {
                User userFromCargo = userRepo.findById(idUserFromCargo);

                chatMessage.setUser(user);

                if (user.getChats() != null && userFromCargo.getChats() != null) {
                    for (Chat c : user.getChats()) {
                        for (Chat cFromCargo : userFromCargo.getChats()) {
                            if (c.getId().equals(cFromCargo.getId())) {
                                chat = chatRepo.findById((long) c.getId());
                            }
                        }
                    }

                    setChatUser(chatMessage, chat, user, userFromCargo);

                    return saveAndSendMessage(chatMessage);
                } else {
                    setChatUser(chatMessage, null, user, userFromCargo);

                    return saveAndSendMessage(chatMessage);
                }
                // If User write LegalUser
            } else {
                System.out.println("USER WRITE LEGAL USER");
                LegalUser legalUserFromCargo = legalUserRepo.findById(idUserFromCargo);

                if (user.getChats() != null && legalUserFromCargo.getChats() != null) {
                    for (Chat c : user.getChats()) {
                        for (Chat cFromCargo : legalUserFromCargo.getChats()) {
                            if (c.getId().equals(cFromCargo.getId())) {
                                chat = chatRepo.findById((long) c.getId());
                            }
                        }
                    }

                    setChatUser(chatMessage, chat, user, legalUserFromCargo);

                    return saveAndSendMessage(chatMessage);
                } else {
                    setChatUser(chatMessage, null, user, legalUserFromCargo);
                    return saveAndSendMessage(chatMessage);
                }
            }
        } else {
            LegalUser legalUser = legalUserRepo.findById(idUser);
            chatMessage.setLegalUser(legalUser);

            // If LegalUser write User
            if (roleUserFromCargo.equals("ROLE_USER")) {
                User userFromCargo = userRepo.findById(idUserFromCargo);

                if (legalUser.getChats() != null && userFromCargo.getChats() != null) {
                    for (Chat c : legalUser.getChats()) {
                        for (Chat cFromCargo : userFromCargo.getChats()) {
                            if (c.getId().equals(cFromCargo.getId())) {
                                chat = chatRepo.findById((long) c.getId());
                            }
                        }
                    }

                    setChatLegalUser(chatMessage, chat, legalUser, userFromCargo);

                    return saveAndSendMessage(chatMessage);
                } else {
                    setChatLegalUser(chatMessage, null, legalUser, userFromCargo);
                    return saveAndSendMessage(chatMessage);
                }
                // If LegalUser write LegalUser
            } else {
                LegalUser legalUserFromCargo = legalUserRepo.findById(idUserFromCargo);

                if (legalUser.getChats() != null && legalUserFromCargo.getChats() != null) {
                    for (Chat c : legalUser.getChats()) {
                        for (Chat cFromCargo : legalUserFromCargo.getChats()) {
                            if (c.getId().equals(cFromCargo.getId())) {
                                chat = chatRepo.findById((long) c.getId());
                            }
                        }
                    }

                    setChatLegalUser(chatMessage, chat, legalUser, legalUserFromCargo);

                    return saveAndSendMessage(chatMessage);
                } else {
                    setChatLegalUser(chatMessage, null, legalUser, legalUserFromCargo);
                    return saveAndSendMessage(chatMessage);
                }
            }
        }
    }

    private ChatMessage saveAndSendMessage(ChatMessage chatMessage) {
        ChatMessage message = chatMessageRepo.save(chatMessage);
        wsSender.accept(EventType.CREATE, message);

        return message;
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

    private void setChatUser(ChatMessage chatMessage, Chat chat, User user, LegalUser userFromCargo) {
        if (chat != null) {
            chatMessage.setChat(chat);
        } else {
            chat = new Chat();
            chatRepo.save(chat);

            chatMessage.setChat(chat);

            user.getChats().add(chat);
            userFromCargo.getChats().add(chat);

            userRepo.save(user);
            legalUserRepo.save(userFromCargo);
        }
    }

    private void setChatLegalUser(ChatMessage chatMessage, Chat chat, LegalUser legalUser, User userFromCargo) {
        if (chat != null) {
            chatMessage.setChat(chat);
        } else {
            chat = new Chat();
            chatRepo.save(chat);

            chatMessage.setChat(chat);

            legalUser.getChats().add(chat);
            userFromCargo.getChats().add(chat);

            legalUserRepo.save(legalUser);
            userRepo.save(userFromCargo);
        }
    }

    private void setChatLegalUser(ChatMessage chatMessage, Chat chat, LegalUser legalUser, LegalUser legalUserFromCargo) {
        if (chat != null) {
            chatMessage.setChat(chat);
        } else {
            chat = new Chat();
            chatRepo.save(chat);

            chatMessage.setChat(chat);

            legalUser.getChats().add(chat);
            legalUserFromCargo.getChats().add(chat);

            legalUserRepo.save(legalUser);
            legalUserRepo.save(legalUserFromCargo);
        }
    }


    public Map<String, Object> getUsersOfChats(long id, String role) {
        Map<String, Object> usersMap = new HashMap<>();
        List<User> users = new ArrayList<>();
        List<LegalUser> legalUsers = new ArrayList<>();

        if (role.equals("ROLE_USER")) {
            User user = userRepo.findById(id);

            for (Chat chat : user.getChats()) {
                User companionUser = userRepo.getByIdChatAndUser(chat.getId(), user.getId());
                LegalUser companionLegalUser = legalUserRepo.getByIdChatAndLegalUser(chat.getId(), 0);

                if (companionUser != null) {
                    users.add(companionUser);
                } else {
                    legalUsers.add(companionLegalUser);
                }
            }

            usersMap.put("users", users);
            usersMap.put("legalUsers", legalUsers);
            usersMap.put("chats", user.getChats());

            return usersMap;
        } else {
            LegalUser legalUser = legalUserRepo.findById(id);

            for (Chat chat : legalUser.getChats()) {
                User companionUser = userRepo.getByIdChatAndUser(chat.getId(), 0);
                LegalUser companionLegalUser = legalUserRepo.getByIdChatAndLegalUser(chat.getId(), legalUser.getId());

                if (companionUser != null) {
                    users.add(companionUser);
                } else {
                    legalUsers.add(companionLegalUser);
                }
            }

            usersMap.put("users", users);
            usersMap.put("legalUsers", legalUsers);
            usersMap.put("chats", legalUser.getChats());

            return usersMap;
        }
    }

    public Map<String, Object> getMessages(long idUser, long idUserCompanion, String role, String roleUserCompanion,
                                           long idChat) {
        Map<String, Object> messagesMap = new HashMap<>();

        List<ChatMessage> messages;

        if (role.equals("ROLE_USER")) {
            User user = userRepo.findById(idUser);

            // If User write User
            if (roleUserCompanion.equals("ROLE_USER")) {
                User userCompanion = userRepo.findById(idUserCompanion);

                messages = chatMessageRepo.findAllByChat_Id(idChat);

                messagesMap.put("user", user);
                messagesMap.put("userCompanion", userCompanion);
                messagesMap.put("messages", messages);

                return messagesMap;
                // If User write LegalUser
            } else {
                LegalUser userCompanion = legalUserRepo.findById(idUserCompanion);

                messages = chatMessageRepo.findAllByChat_Id(idChat);

                messagesMap.put("user", user);
                messagesMap.put("userCompanion", userCompanion);
                messagesMap.put("messages", messages);

                return messagesMap;
            }
        } else {
            LegalUser legalUser = legalUserRepo.findById(idUser);

            // If LegalUser write User
            if (roleUserCompanion.equals("ROLE_USER")) {
                User userCompanion = userRepo.findById(idUserCompanion);

                messages = chatMessageRepo.findAllByChat_Id(idChat);

                messagesMap.put("user", legalUser);
                messagesMap.put("userCompanion", userCompanion);
                messagesMap.put("messages", messages);

                return messagesMap;
                // If LegalUser write LegalUser
            } else {
                LegalUser userCompanion = legalUserRepo.findById(idUserCompanion);

                messages = chatMessageRepo.findAllByChat_Id(idChat);

                messagesMap.put("user", legalUser);
                messagesMap.put("userCompanion", userCompanion);
                messagesMap.put("messages", messages);

                return messagesMap;
            }
        }
    }
}
