package com.project.Expensedivider.config;

import com.project.Expensedivider.user.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class OauthSuccessHandler implements AuthenticationSuccessHandler {
    @Lazy
    @Autowired
    private   UserService userService;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // Save user if not already present
        JwtResponseDto authResponse = userService.handleOauthAuthentication(name, email, response);
        String accessToken = authResponse.getAccessToken();
        String refreshToken = authResponse.getRefreshToken();
        String redirectUrl = String.format(
                "http://localhost:3000/dashboard/overview?accessToken=%s&refreshToken=%s",
                accessToken, refreshToken
        );
        // Redirect to frontend with JWT
        response.sendRedirect(redirectUrl);
    }
}
