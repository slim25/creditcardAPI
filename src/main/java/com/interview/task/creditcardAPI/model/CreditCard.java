package com.interview.task.creditcardAPI.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String maskedCardNumber;
    private String cardHolderName;
    private String expiryDate;

    @Column(name = "card_token", unique = true, nullable = false)
    private String cardToken;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
