package br.com.fiap.clyvo_companion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class PrescricaoRequestDTO {

    @NotNull
    private Long idPet;

    @NotBlank
    @Size(max = 100)
    private String nomeMedicamento;

    @NotBlank
    @Size(max = 50)
    private String dsDosagem;

    @NotNull
    @Positive
    private Integer frequenciaHoras;

    @NotNull
    private LocalDate dtInicio;

    private LocalDate dtFim;
}
