package br.com.fiap.clyvo_companion.dto;

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
public class LogSaudeAlertaDTO {

    private Long idLog;
    private Long idPet;
    private LocalDateTime dtRegistro;
    private BigDecimal vlMetrica;
    private String metrica;
    private String obs;
    private String motivoAlerta;
    private String nivelAlerta;
}
