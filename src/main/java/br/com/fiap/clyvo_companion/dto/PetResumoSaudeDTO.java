package br.com.fiap.clyvo_companion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Visão consolidada da saúde e cuidados do pet (regra de negócio além do CRUD).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetResumoSaudeDTO {

    private PetResponseDTO pet;
    private List<PrescricaoResponseDTO> prescricoesAtivas = new ArrayList<>();
    private List<LogSaudeResponseDTO> ultimosLogs = new ArrayList<>();
    private AgendamentoResponseDTO proximoAgendamento;
    private int totalPrescricoesAtivas;
    private int totalLogsRecentes;
}
