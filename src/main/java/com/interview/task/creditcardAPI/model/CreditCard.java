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

    @Column(unique = true)
    private String cardToken;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
