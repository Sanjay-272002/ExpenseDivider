package com.project.Expensedivider.config;

import com.project.Expensedivider.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LogoutHandler logoutHandler;
    private final OauthSuccessHandler oauthsuccessHandler;
    private final UserService userService;

    private static final String[] WHITE_LIST_URL = {"/auth/**",
            "/email/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"};




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Updated method for disabling CSRF
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(WHITE_LIST_URL)
                        .permitAll() // Matchers updated to AntPathRequestMatcher
                        .anyRequest().authenticated()
                ).cors(Customizer.withDefaults()).headers(headers -> headers
                        .httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable) // Disable HSTS in dev
                ).oauth2Login(oauth2 -> oauth2
                        .successHandler(oauthsuccessHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session configuration
                )
                .authenticationProvider(authenticationProvider) // Set the custom authentication provider
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/auth/logout")
                                .addLogoutHandler((request, response, auth) -> {
                                    for (Cookie cookie : request.getCookies()) {
                                        String cookieName = cookie.getName();
                                        Cookie cookieToDelete = new Cookie(cookieName, null);
                                        cookieToDelete.setMaxAge(0);
                                        cookieToDelete.setPath("/");
                                        cookieToDelete.setHttpOnly(true);
                                        response.addCookie(cookieToDelete);
                                    }
                                })
                                .logoutSuccessHandler((request, response, authentication) -> {
                                    System.out.println("logout handler works");
                                    SecurityContextHolder.clearContext();
//                                   userService.setJwtCookie(response,"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYW5qYXlrdW1hcmFubmE3OTlAZ21haWwuY29tIiwiaWF0IjoxNzQ2MjA0MjE0LCJleHAiOjE3NDYyMDc4MTR9.3oiWJKqZx6eeJ6XI_8gYpBGPiTH6-_JNSylSe4NrebI",true);
//                                   userService.setRefreshTokenCookie(response,"334455",true);
//                                    response.setStatus(HttpServletResponse.SC_OK);
                                })
                ).exceptionHandling(exceptionHandling -> exceptionHandling
                        // Handle AccessDeniedException (403)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"error\": \"Access denied\", \"message\": \"" + accessDeniedException.getMessage() + "\"}");
                        })
                        // Handle AuthenticationException (401)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
                        })
                );

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000","https://evenup.vercel.app"));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Set-Cookie"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**",configuration);

        return source;
    }
}