package br.com.fiap.clyvo_companion.dto;

import br.com.fiap.clyvo_companion.model.Prescricao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescricaoResponseDTO {

    private Long idPrescricao;
    private Long idPet;
    private String nomeMedicamento;
    private String dsDosagem;
    private Integer frequenciaHoras;
    private LocalDate dtInicio;
    private LocalDate dtFim;

    public static PrescricaoResponseDTO from(Prescricao prescricao) {
        PrescricaoResponseDTO dto = new PrescricaoResponseDTO();
        dto.setIdPrescricao(prescricao.getIdPrescricao());
        dto.setIdPet(prescricao.getPet().getIdPet());
        dto.setNomeMedicamento(prescricao.getNomeMedicamento());
        dto.setDsDosagem(prescricao.getDsDosagem());
        dto.setFrequenciaHoras(prescricao.getFrequenciaHoras());
        dto.setDtInicio(prescricao.getDtInicio());
        dto.setDtFim(prescricao.getDtFim());
        return dto;
    }
}
