package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.dto.AgendamentoResponseDTO;
import br.com.fiap.clyvo_companion.dto.LogSaudeResponseDTO;
import br.com.fiap.clyvo_companion.dto.PetResumoSaudeDTO;
import br.com.fiap.clyvo_companion.dto.PetResponseDTO;
import br.com.fiap.clyvo_companion.dto.PrescricaoResponseDTO;
import br.com.fiap.clyvo_companion.exception.ResourceNotFoundException;
import br.com.fiap.clyvo_companion.model.Pet;
import br.com.fiap.clyvo_companion.repository.AgendamentoRepository;
import br.com.fiap.clyvo_companion.repository.LogSaudeRepository;
import br.com.fiap.clyvo_companion.repository.PetRepository;
import br.com.fiap.clyvo_companion.repository.PrescricaoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Consolida prescrições, logs e próximo agendamento — separado do CRUD de Pet.
 */
@Service
public class PetResumoSaudeService {

    private final PetRepository petRepository;
    private final PrescricaoRepository prescricaoRepository;
    private final LogSaudeRepository logSaudeRepository;
    private final AgendamentoRepository agendamentoRepository;

    public PetResumoSaudeService(
            PetRepository petRepository,
            PrescricaoRepository prescricaoRepository,
            LogSaudeRepository logSaudeRepository,
            AgendamentoRepository agendamentoRepository) {
        this.petRepository = petRepository;
        this.prescricaoRepository = prescricaoRepository;
        this.logSaudeRepository = logSaudeRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "petsResumo", key = "#id")
    public PetResumoSaudeDTO buscar(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado: " + id));
        LocalDate hoje = LocalDate.now();
        LocalDateTime agora = LocalDateTime.now();

        List<PrescricaoResponseDTO> prescricoesAtivas = prescricaoRepository
                .findAtivasPorPet(id, hoje)
                .stream()
                .map(PrescricaoResponseDTO::from)
                .toList();

        List<LogSaudeResponseDTO> ultimosLogs = logSaudeRepository
                .findTop5ByPetIdPetOrderByDtRegistroDesc(id)
                .stream()
                .map(LogSaudeResponseDTO::from)
                .toList();

        AgendamentoResponseDTO proximoAgendamento = agendamentoRepository
                .findProximosPorPet(id, agora)
                .stream()
                .findFirst()
                .map(AgendamentoResponseDTO::from)
                .orElse(null);

        PetResumoSaudeDTO resumo = new PetResumoSaudeDTO();
        resumo.setPet(PetResponseDTO.from(pet));
        resumo.setPrescricoesAtivas(prescricoesAtivas);
        resumo.setUltimosLogs(ultimosLogs);
        resumo.setProximoAgendamento(proximoAgendamento);
        resumo.setTotalPrescricoesAtivas(prescricoesAtivas.size());
        resumo.setTotalLogsRecentes(ultimosLogs.size());
        return resumo;
    }
}
