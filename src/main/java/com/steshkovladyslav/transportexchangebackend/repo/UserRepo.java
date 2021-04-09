package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepo extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"cargo", "transports"})
    User findById(long id);

    @EntityGraph(attributePaths = {"cargo", "transports"})
    User findByEmail(String email);

    @Query(value = "SELECT * FROM users u, user_chats uc where uc.chat_id = :idChat and " +
            "uc.user_id != :idUser", nativeQuery = true)
    User getByIdChatAndUser(long idChat, long idUser);
}
