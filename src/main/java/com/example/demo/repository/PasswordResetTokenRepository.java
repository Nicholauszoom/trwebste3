package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository <PasswordResetToken, Long>{

    PasswordResetToken findByToken(String token);
}
