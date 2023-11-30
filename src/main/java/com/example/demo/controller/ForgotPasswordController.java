package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.appuser.AppUser;
import com.example.demo.appuser.AppUserRepository;
import com.example.demo.appuser.AppUserService;
import com.example.demo.email.EmailSender;
import com.example.demo.email.EmailService;
import com.example.demo.model.ForgotPasswordForm;
import com.example.demo.model.PasswordResetToken;
import com.example.demo.repository.PasswordResetTokenRepository;

@Controller
public class ForgotPasswordController {
    @Autowired
    private AppUserService userService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

       @Autowired
    private EmailSender emailSender;

      @Autowired
    private AppUserRepository appUserRepository;


    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("forgotPasswordForm", new ForgotPasswordForm());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPasswordForm(@ModelAttribute("forgotPasswordForm") ForgotPasswordForm form,
                                            HttpServletRequest request) {
        AppUser user = appUserRepository.findByEmail(form.getEmail());
        if (user != null) {
            // Generate a unique token
            String token = UUID.randomUUID().toString();

            // Create a PasswordResetToken
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(24));

            // Save the token
            tokenRepository.save(resetToken);

            // Send password reset email
            String resetUrl = getResetUrl(request, token);
            emailSender.sendPasswordResetEmaill(user.getEmail(), resetUrl);
        }

        return "redirect:/forgot-password?success";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "redirect:/forgot-password?error";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPasswordForm(@RequestParam("token") String token,
                                           @RequestParam("password") String password) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "redirect:/forgot-password?error";
        }

        AppUser user = resetToken.getUser();
        user.setPassword(password);
        ((CrudRepository<AppUser, Long>) userService).save(user);

        tokenRepository.delete(resetToken);

        return "redirect:/login?resetSuccess";
    }

    private String getResetUrl(HttpServletRequest request, String token) {
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return baseUrl + "/reset-password?token=" + token;
    }
}