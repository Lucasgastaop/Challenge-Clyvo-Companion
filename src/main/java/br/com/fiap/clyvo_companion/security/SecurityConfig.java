package br.com.fiap.clyvo_companion.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/auth/login",
                                "/css/**",
                                "/images/**",
                                "/js/**",
                                "/error",
                                "/acesso-negado").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/v3/api-docs/**").permitAll()
                        .requestMatchers("/tutor/**").hasRole("TUTOR")
                        .requestMatchers("/vet/**").hasRole("VETERINARIO")
                        .requestMatchers(HttpMethod.POST, "/logs-saude", "/logs-saude/**").hasRole("TUTOR")
                        .requestMatchers(HttpMethod.PUT, "/logs-saude/**").hasRole("TUTOR")
                        .requestMatchers(HttpMethod.POST, "/prescricoes", "/prescricoes/**").hasRole("VETERINARIO")
                        .requestMatchers(HttpMethod.PUT, "/prescricoes/**").hasRole("VETERINARIO")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/acesso-negado")
                        .defaultAuthenticationEntryPointFor(
                                (request, response, authException) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                    response.getWriter().write(
                                            "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Não autenticado\"}");
                                },
                                PathPatternRequestMatcher.pathPattern("/auth/**")))
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/h2-console/**",
                        "/auth/**",
                        "/usuarios/**",
                        "/pets/**",
                        "/clinicas/**",
                        "/prescricoes/**",
                        "/logs-saude/**",
                        "/agendamentos/**",
                        "/logs-sistema/**",
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
