package br.com.fiap.clyvo_companion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PetRequestDTO(
        @NotNull Long idUsuario,
        @NotBlank @Size(max = 100) String nomePet,
        @NotBlank @Size(max = 50) String especie,
        LocalDate dtNascimento
) {
}
