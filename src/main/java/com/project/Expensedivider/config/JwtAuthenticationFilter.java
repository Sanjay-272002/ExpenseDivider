package com.project.Expensedivider.config;


import com.project.Expensedivider.token.TokenRepository;
import com.project.Expensedivider.user.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
   // private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

//    private void handleAuthenticationException(HttpServletResponse response, AuthenticationException ex) throws IOException {
//        response.setContentType("application/json");
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
//        response.getWriter().write("{\"error\": \"Authentication failed. Please provide valid credentials.\"}");
//    }
//
//    private void handleAccessDeniedException(HttpServletResponse response, AccessDeniedException ex) throws IOException {
//        response.setContentType("application/json");
//        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
//        response.getWriter().write("{\"error\": \"Access denied. You do not have permission to access this resource.\"}");
//    }
//
//    private void handleGenericException(HttpServletResponse response, Exception ex) throws IOException {
//        response.setContentType("application/json");
//        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
//        response.getWriter().write("{\"error\": \"An unexpected error occurred. Please try again later.\"}");
//    }

    @Override
    protected void doFilterInternal (
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("jwt");
        if (request.getServletPath().contains("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

//        try {
            final String jwt = authHeader.substring(7);
            System.out.println("jwt"+" "+jwt);
            final String userEmail = jwtService.extractUsername(jwt);
          //  System.out.println("jwt: "+jwt+"userEmail  "+userEmail);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          //  System.out.println("Authentication passed");
            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                var isTokenValid = tokenRepository.findByToken(jwt)
                        .map(t -> !t.isExpired() && !t.isRevoked())
                        .orElse(false);
                if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                   // System.out.println("JWtAuthentication passed");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
//        } catch (AuthenticationException authEx) {
//            handleAuthenticationException(response, authEx);
//        } catch (AccessDeniedException accessEx) {
//            handleAccessDeniedException(response, accessEx);
//        } catch (Exception ex) {
//            handleGenericException(response, ex);
//        }
    }
}