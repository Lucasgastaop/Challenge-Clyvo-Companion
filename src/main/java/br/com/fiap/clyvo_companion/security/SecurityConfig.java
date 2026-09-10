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
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLICOS = {
            "/login",
            "/auth/login",
            "/css/**",
            "/images/**",
            "/js/**",
            "/error",
            "/acesso-negado"
    };

    private static final String[] DOCUMENTACAO = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**"
    };

    private static final String[] RECURSOS_API = {
            "/auth/**",
            "/usuarios/**",
            "/pets/**",
            "/clinicas/**",
            "/prescricoes/**",
            "/logs-saude/**",
            "/agendamentos/**",
            "/logs-sistema/**"
    };

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
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(PUBLICOS).permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(DOCUMENTACAO).permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
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
                                matcherApi()))
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers(csrfIgnorados()))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    private static RequestMatcher matcherApi() {
        RequestMatcher[] matchers = Arrays.stream(RECURSOS_API)
                .map(path -> (RequestMatcher) PathPatternRequestMatcher.pathPattern(path))
                .toArray(RequestMatcher[]::new);
        return new OrRequestMatcher(matchers);
    }

    private static String[] csrfIgnorados() {
        String[] ignorados = new String[RECURSOS_API.length + DOCUMENTACAO.length + 1];
        ignorados[0] = "/h2-console/**";
        System.arraycopy(RECURSOS_API, 0, ignorados, 1, RECURSOS_API.length);
        System.arraycopy(DOCUMENTACAO, 0, ignorados, 1 + RECURSOS_API.length, DOCUMENTACAO.length);
        return ignorados;
    }
}
