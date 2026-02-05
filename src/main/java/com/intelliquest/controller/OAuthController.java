package com.intelliquest.controller;

import com.intelliquest.model.User;
import com.intelliquest.security.JwtService;
import com.intelliquest.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class OAuthController {

    private final JwtService jwtService;
    private final UserService userService;

    public OAuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @GetMapping("/oauth2/success")
    public Map<String, String> getToken(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            String role = userOpt.get().getRole().name();
            String token = jwtService.generateToken(email, name, picture, role);
            return Map.of("token", token);
        } else {
            throw new RuntimeException("User not found in DB for email: " + email);
        }
    }
}
