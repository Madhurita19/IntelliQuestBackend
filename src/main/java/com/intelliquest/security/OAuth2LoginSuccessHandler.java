package com.intelliquest.security;

import com.intelliquest.model.User;
import com.intelliquest.model.User.UserRole;
import com.intelliquest.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final String frontendUrl;

    public OAuth2LoginSuccessHandler(
        JwtService jwtService,
        UserRepository userRepository,
        @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            user = new User();
            user.setUsername(name);
            user.setEmail(email);
            user.setPassword(UUID.randomUUID().toString());
            user.setRole(UserRole.USER);
            user = userRepository.save(user);
        }
        
        String jwt = jwtService.generateToken(email, name, picture, user.getRole().name());
        response.sendRedirect(frontendUrl + "/oauth-success?token=" + jwt);
    }
}
