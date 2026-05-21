package br.com.fiap.clyvo_companion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicaRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String nomeClinica;

    @NotBlank
    @Size(min = 14, max = 14)
    private String cnpj;

    @NotBlank
    @Size(max = 200)
    private String endereco;

    @Size(max = 15)
    private String telefone;
}
