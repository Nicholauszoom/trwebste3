package com.example.demo.email;

public interface EmailSender {
    void send(String from, String email);
    public void sendPasswordResetEmaill(String recipientEmail, String resetUrl);

}
