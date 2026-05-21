package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.dto.*;
import br.com.fiap.clyvo_companion.exception.ResourceNotFoundException;
import br.com.fiap.clyvo_companion.model.Pet;
import br.com.fiap.clyvo_companion.model.Usuario;
import br.com.fiap.clyvo_companion.repository.AgendamentoRepository;
import br.com.fiap.clyvo_companion.repository.LogSaudeRepository;
import br.com.fiap.clyvo_companion.repository.PetRepository;
import br.com.fiap.clyvo_companion.repository.PrescricaoRepository;
import br.com.fiap.clyvo_companion.repository.UsuarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrescricaoRepository prescricaoRepository;
    private final LogSaudeRepository logSaudeRepository;
    private final AgendamentoRepository agendamentoRepository;

    public PetService(
            PetRepository petRepository,
            UsuarioRepository usuarioRepository,
            PrescricaoRepository prescricaoRepository,
            LogSaudeRepository logSaudeRepository,
            AgendamentoRepository agendamentoRepository) {
        this.petRepository = petRepository;
        this.usuarioRepository = usuarioRepository;
        this.prescricaoRepository = prescricaoRepository;
        this.logSaudeRepository = logSaudeRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "pets", key = "#id")
    public PetResponseDTO buscarPorId(Long id) {
        return PetResponseDTO.from(buscarEntidade(id));
    }

    /**
     * Consolida prescrições ativas, últimos logs de saúde e próximo agendamento do pet.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "petsResumo", key = "#id")
    public PetResumoSaudeDTO buscarResumoSaude(Long id) {
        Pet pet = buscarEntidade(id);
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

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> listar(String nome, String especie, Long idUsuario, Pageable pageable) {
        return petRepository.buscarComFiltros(nome, especie, idUsuario, pageable)
                .map(PetResponseDTO::from);
    }

    @Transactional
    @CacheEvict(value = "pets", allEntries = true)
    public PetResponseDTO criar(PetRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + dto.getIdUsuario()));

        Pet pet = Pet.builder()
                .usuario(usuario)
                .nomePet(dto.getNomePet())
                .especie(dto.getEspecie())
                .dtNascimento(dto.getDtNascimento())
                .dtCadastro(LocalDate.now())
                .build();

        return PetResponseDTO.from(petRepository.save(pet));
    }

    @Transactional
    @CacheEvict(value = "pets", allEntries = true)
    public PetResponseDTO atualizar(Long id, PetRequestDTO dto) {
        Pet pet = buscarEntidade(id);
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + dto.getIdUsuario()));

        pet.setUsuario(usuario);
        pet.setNomePet(dto.getNomePet());
        pet.setEspecie(dto.getEspecie());
        pet.setDtNascimento(dto.getDtNascimento());

        return PetResponseDTO.from(petRepository.save(pet));
    }

    @Transactional
    @CacheEvict(value = "pets", allEntries = true)
    public void excluir(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pet não encontrado: " + id);
        }
        petRepository.deleteById(id);
    }

    private Pet buscarEntidade(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado: " + id));
    }
}
