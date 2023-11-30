package com.example.demo.model;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.example.demo.appuser.AppUser;

import lombok.Data;

@Data
@Entity
public class PasswordResetToken {
   @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = AppUser.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private AppUser user;

    private LocalDateTime expiryDate;

}

