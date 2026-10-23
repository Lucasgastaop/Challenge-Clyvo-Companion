package br.com.fiap.clyvo_companion.dto;

import br.com.fiap.clyvo_companion.security.UsuarioDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenResponseDTO {

    private Long idUsuario;
    private String nomeUsuario;
    private String email;
    private String tpPerfil;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;

    public static AuthTokenResponseDTO of(
            UsuarioDetails usuario,
            String accessToken,
            String refreshToken,
            long expiresIn) {
        return new AuthTokenResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNomeUsuario(),
                usuario.getUsername(),
                usuario.getTpPerfil(),
                accessToken,
                refreshToken,
                "Bearer",
                expiresIn);
    }
}
