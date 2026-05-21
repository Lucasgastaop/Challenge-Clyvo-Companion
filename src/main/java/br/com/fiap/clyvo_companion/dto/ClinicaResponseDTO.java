package br.com.fiap.clyvo_companion.dto;

import br.com.fiap.clyvo_companion.model.Clinica;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicaResponseDTO {

    private Long idClinica;
    private String nomeClinica;
    private String cnpj;
    private String endereco;
    private String telefone;

    public static ClinicaResponseDTO from(Clinica clinica) {
        ClinicaResponseDTO dto = new ClinicaResponseDTO();
        dto.setIdClinica(clinica.getIdClinica());
        dto.setNomeClinica(clinica.getNomeClinica());
        dto.setCnpj(clinica.getCnpj());
        dto.setEndereco(clinica.getEndereco());
        dto.setTelefone(clinica.getTelefone());
        return dto;
    }
}
