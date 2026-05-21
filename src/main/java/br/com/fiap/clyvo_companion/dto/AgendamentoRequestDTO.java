package br.com.fiap.clyvo_companion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoRequestDTO {

    @NotNull
    private Long idPet;

    @NotNull
    private Long idClinica;

    @NotNull
    private LocalDateTime dtAgenda;

    @NotBlank
    @Size(max = 50)
    private String tipoServico;

    @NotBlank
    @Size(max = 20)
    private String status;
}
