package br.com.fiap.clyvo_companion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class LogSaudeRequestDTO {

    @NotNull
    private Long idPet;

    @NotNull
    private LocalDateTime dtRegistro;

    @NotNull
    private BigDecimal vlMetrica;

    @NotBlank
    @Size(max = 30)
    private String metrica;

    @Size(max = 255)
    private String obs;
}
