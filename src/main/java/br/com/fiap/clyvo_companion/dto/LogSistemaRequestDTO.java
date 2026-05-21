package br.com.fiap.clyvo_companion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogSistemaRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String nomeProc;

    @Size(max = 100)
    private String nomeUsuario;

    @NotNull
    private LocalDate dtErro;

    @NotNull
    private Integer cdErro;

    @NotBlank
    @Size(max = 400)
    private String msgErro;
}
