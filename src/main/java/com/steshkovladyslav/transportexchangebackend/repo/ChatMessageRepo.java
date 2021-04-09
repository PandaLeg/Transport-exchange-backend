package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.ChatMessage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepo extends JpaRepository<ChatMessage, Long> {
    @EntityGraph(attributePaths = {"user", "legalUser", "chat"})
    List<ChatMessage> findAllByUser_Id(long id);

    @EntityGraph(attributePaths = {"user", "legalUser", "chat"})
    List<ChatMessage> findAllByLegalUser_Id(long id);

    @EntityGraph(attributePaths = {"user", "legalUser", "chat"})
    List<ChatMessage> findAllByChat_Id(long id);
}
