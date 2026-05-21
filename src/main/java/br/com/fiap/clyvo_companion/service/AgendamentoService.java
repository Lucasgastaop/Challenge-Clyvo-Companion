package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.dto.AgendamentoRequestDTO;
import br.com.fiap.clyvo_companion.dto.AgendamentoResponseDTO;
import br.com.fiap.clyvo_companion.dto.AgendamentoStatusDTO;
import br.com.fiap.clyvo_companion.exception.ResourceNotFoundException;
import br.com.fiap.clyvo_companion.model.Agendamento;
import br.com.fiap.clyvo_companion.model.Clinica;
import br.com.fiap.clyvo_companion.model.Pet;
import br.com.fiap.clyvo_companion.model.enums.StatusAgendamento;
import br.com.fiap.clyvo_companion.repository.AgendamentoRepository;
import br.com.fiap.clyvo_companion.repository.ClinicaRepository;
import br.com.fiap.clyvo_companion.repository.PetRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final PetRepository petRepository;
    private final ClinicaRepository clinicaRepository;
    private final AgendamentoStatusValidator statusValidator;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            PetRepository petRepository,
            ClinicaRepository clinicaRepository,
            AgendamentoStatusValidator statusValidator) {
        this.agendamentoRepository = agendamentoRepository;
        this.petRepository = petRepository;
        this.clinicaRepository = clinicaRepository;
        this.statusValidator = statusValidator;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "agendamentos", key = "#id")
    public AgendamentoResponseDTO buscarPorId(Long id) {
        return AgendamentoResponseDTO.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Page<AgendamentoResponseDTO> listar(Long idPet, Long idClinica, String status, Pageable pageable) {
        return agendamentoRepository.buscarComFiltros(idPet, idClinica, status, pageable)
                .map(AgendamentoResponseDTO::from);
    }

    @Transactional
    @CacheEvict(value = {"agendamentos", "petsResumo"}, allEntries = true)
    public AgendamentoResponseDTO criar(AgendamentoRequestDTO dto) {
        Pet pet = petRepository.findById(dto.getIdPet())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado: " + dto.getIdPet()));
        Clinica clinica = clinicaRepository.findById(dto.getIdClinica())
                .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada: " + dto.getIdClinica()));

        Agendamento agendamento = Agendamento.builder()
                .pet(pet)
                .clinica(clinica)
                .dtAgenda(dto.getDtAgenda())
                .tipoServico(dto.getTipoServico())
                .status(dto.getStatus())
                .build();

        return AgendamentoResponseDTO.from(agendamentoRepository.save(agendamento));
    }

    /**
     * Atualiza somente o status com validação de transições permitidas.
     * AGENDADO → CONFIRMADO | CANCELADO
     * CONFIRMADO → CONCLUIDO | CANCELADO
     */
    @Transactional
    @CacheEvict(value = {"agendamentos", "petsResumo"}, allEntries = true)
    public AgendamentoResponseDTO atualizarStatus(Long id, AgendamentoStatusDTO dto) {
        Agendamento agendamento = buscarEntidade(id);
        StatusAgendamento atual = StatusAgendamento.fromValor(agendamento.getStatus());
        StatusAgendamento novo = StatusAgendamento.fromValor(dto.getStatus());

        statusValidator.validarTransicao(atual, novo);
        agendamento.setStatus(novo.name());

        return AgendamentoResponseDTO.from(agendamentoRepository.save(agendamento));
    }

    @Transactional
    @CacheEvict(value = {"agendamentos", "petsResumo"}, allEntries = true)
    public AgendamentoResponseDTO atualizar(Long id, AgendamentoRequestDTO dto) {
        Agendamento agendamento = buscarEntidade(id);
        Pet pet = petRepository.findById(dto.getIdPet())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado: " + dto.getIdPet()));
        Clinica clinica = clinicaRepository.findById(dto.getIdClinica())
                .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada: " + dto.getIdClinica()));

        agendamento.setPet(pet);
        agendamento.setClinica(clinica);
        agendamento.setDtAgenda(dto.getDtAgenda());
        agendamento.setTipoServico(dto.getTipoServico());
        agendamento.setStatus(dto.getStatus());

        return AgendamentoResponseDTO.from(agendamentoRepository.save(agendamento));
    }

    @Transactional
    @CacheEvict(value = {"agendamentos", "petsResumo"}, allEntries = true)
    public void excluir(Long id) {
        if (!agendamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agendamento não encontrado: " + id);
        }
        agendamentoRepository.deleteById(id);
    }

    private Agendamento buscarEntidade(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado: " + id));
    }
}
