package br.com.fiap.clyvo_companion.dto;

import br.com.fiap.clyvo_companion.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long idUsuario;
    private String nomeUsuario;
    private String email;
    private String tpPerfil;
    private LocalDate dtCadastro;

    public static UsuarioResponseDTO from(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNomeUsuario(usuario.getNomeUsuario());
        dto.setEmail(usuario.getEmail());
        dto.setTpPerfil(usuario.getTpPerfil());
        dto.setDtCadastro(usuario.getDtCadastro());
        return dto;
    }
}
