package br.com.fiap.clyvo_companion.controller;

import br.com.fiap.clyvo_companion.dto.LoginRequestDTO;
import br.com.fiap.clyvo_companion.dto.LoginResponseDTO;
import br.com.fiap.clyvo_companion.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(
            @Valid @RequestBody LoginRequestDTO dto,
            HttpServletRequest request,
            HttpServletResponse response) {
        return authService.login(dto, request, response);
    }

    @GetMapping("/me")
    public LoginResponseDTO me() {
        return authService.me();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
    }
}
