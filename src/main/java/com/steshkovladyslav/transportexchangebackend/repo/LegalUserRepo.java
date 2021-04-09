package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.LegalUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LegalUserRepo extends JpaRepository<LegalUser, Long> {
    @EntityGraph(attributePaths = {"cargo", "transports"})
    LegalUser findById(long id);

    @EntityGraph(attributePaths = {"cargo", "transports"})
    LegalUser findByEmail(String email);

    @Query(value = "SELECT * FROM legal_users lu, legal_user_chats luc where luc.chat_id = :idChat and " +
            "luc.legal_user_id != :idLegalUser", nativeQuery = true)
    LegalUser getByIdChatAndLegalUser(long idChat, long idLegalUser);
}
