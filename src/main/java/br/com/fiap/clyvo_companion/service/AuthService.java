package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.dto.LoginRequestDTO;
import br.com.fiap.clyvo_companion.dto.LoginResponseDTO;
import br.com.fiap.clyvo_companion.exception.UnauthorizedException;
import br.com.fiap.clyvo_companion.security.UsuarioAutenticadoService;
import br.com.fiap.clyvo_companion.security.UsuarioDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioAutenticadoService usuarioAutenticadoService) {
        this.authenticationManager = authenticationManager;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public LoginResponseDTO login(
            LoginRequestDTO dto,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha()));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            return LoginResponseDTO.from((UsuarioDetails) authentication.getPrincipal());
        } catch (AuthenticationException ex) {
            throw new UnauthorizedException("E-mail ou senha inválidos");
        }
    }

    public LoginResponseDTO me() {
        return LoginResponseDTO.from(usuarioAutenticadoService.exigirUsuarioLogado());
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        securityContextRepository.saveContext(
                SecurityContextHolder.createEmptyContext(), request, response);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
