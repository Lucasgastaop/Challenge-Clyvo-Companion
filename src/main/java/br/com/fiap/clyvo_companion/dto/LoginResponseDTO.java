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
public class LoginResponseDTO {

    private Long idUsuario;
    private String nomeUsuario;
    private String email;
    private String tpPerfil;

    public static LoginResponseDTO from(UsuarioDetails usuario) {
        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNomeUsuario(),
                usuario.getUsername(),
                usuario.getTpPerfil());
    }
}
