package br.com.fiap.clyvo_companion.dto;

import br.com.fiap.clyvo_companion.model.Agendamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoResponseDTO {

    private Long idAgendamento;
    private Long idPet;
    private Long idClinica;
    private LocalDateTime dtAgenda;
    private String tipoServico;
    private String status;

    public static AgendamentoResponseDTO from(Agendamento agendamento) {
        AgendamentoResponseDTO dto = new AgendamentoResponseDTO();
        dto.setIdAgendamento(agendamento.getIdAgendamento());
        dto.setIdPet(agendamento.getPet().getIdPet());
        dto.setIdClinica(agendamento.getClinica().getIdClinica());
        dto.setDtAgenda(agendamento.getDtAgenda());
        dto.setTipoServico(agendamento.getTipoServico());
        dto.setStatus(agendamento.getStatus());
        return dto;
    }
}
