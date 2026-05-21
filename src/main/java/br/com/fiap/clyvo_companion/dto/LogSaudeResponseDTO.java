package br.com.fiap.clyvo_companion.dto;

import br.com.fiap.clyvo_companion.model.LogSaude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogSaudeResponseDTO {

    private Long idLog;
    private Long idPet;
    private LocalDateTime dtRegistro;
    private BigDecimal vlMetrica;
    private String metrica;
    private String obs;

    public static LogSaudeResponseDTO from(LogSaude log) {
        LogSaudeResponseDTO dto = new LogSaudeResponseDTO();
        dto.setIdLog(log.getIdLog());
        dto.setIdPet(log.getPet().getIdPet());
        dto.setDtRegistro(log.getDtRegistro());
        dto.setVlMetrica(log.getVlMetrica());
        dto.setMetrica(log.getMetrica());
        dto.setObs(log.getObs());
        return dto;
    }
}
