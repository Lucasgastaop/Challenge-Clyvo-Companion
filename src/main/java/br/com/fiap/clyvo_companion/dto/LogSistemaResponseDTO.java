package br.com.fiap.clyvo_companion.dto;

import br.com.fiap.clyvo_companion.model.LogSistema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogSistemaResponseDTO {

    private Long idErro;
    private String nomeProc;
    private String nomeUsuario;
    private LocalDate dtErro;
    private Integer cdErro;
    private String msgErro;

    public static LogSistemaResponseDTO from(LogSistema log) {
        LogSistemaResponseDTO dto = new LogSistemaResponseDTO();
        dto.setIdErro(log.getIdErro());
        dto.setNomeProc(log.getNomeProc());
        dto.setNomeUsuario(log.getNomeUsuario());
        dto.setDtErro(log.getDtErro());
        dto.setCdErro(log.getCdErro());
        dto.setMsgErro(log.getMsgErro());
        return dto;
    }
}
