package com.steshkovladyslav.transportexchangebackend.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "chat_messages")
@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
public class ChatMessage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String message;

    @ManyToOne
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    @JoinColumn(name = "legal_user_id")
    private LegalUser legalUser;

    @ManyToOne
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    @JoinColumn(name = "chat_id")
    private Chat chat;

    public ChatMessage() {
    }

    public ChatMessage(String message) {
        this.message = message;
    }
}
