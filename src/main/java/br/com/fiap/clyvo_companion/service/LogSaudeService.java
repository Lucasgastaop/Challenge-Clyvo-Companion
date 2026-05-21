package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.dto.LogSaudeRequestDTO;
import br.com.fiap.clyvo_companion.dto.LogSaudeResponseDTO;
import br.com.fiap.clyvo_companion.exception.ResourceNotFoundException;
import br.com.fiap.clyvo_companion.model.LogSaude;
import br.com.fiap.clyvo_companion.model.Pet;
import br.com.fiap.clyvo_companion.repository.LogSaudeRepository;
import br.com.fiap.clyvo_companion.repository.PetRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogSaudeService {

    private final LogSaudeRepository logSaudeRepository;
    private final PetRepository petRepository;

    public LogSaudeService(LogSaudeRepository logSaudeRepository, PetRepository petRepository) {
        this.logSaudeRepository = logSaudeRepository;
        this.petRepository = petRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "logsSaude", key = "#id")
    public LogSaudeResponseDTO buscarPorId(Long id) {
        return LogSaudeResponseDTO.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Page<LogSaudeResponseDTO> listar(Long idPet, String metrica, Pageable pageable) {
        return logSaudeRepository.buscarComFiltros(idPet, metrica, pageable)
                .map(LogSaudeResponseDTO::from);
    }

    @Transactional
    @CacheEvict(value = "logsSaude", allEntries = true)
    public LogSaudeResponseDTO criar(LogSaudeRequestDTO dto) {
        Pet pet = petRepository.findById(dto.getIdPet())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado: " + dto.getIdPet()));

        LogSaude log = LogSaude.builder()
                .pet(pet)
                .dtRegistro(dto.getDtRegistro())
                .vlMetrica(dto.getVlMetrica())
                .metrica(dto.getMetrica())
                .obs(dto.getObs())
                .build();

        return LogSaudeResponseDTO.from(logSaudeRepository.save(log));
    }

    @Transactional
    @CacheEvict(value = "logsSaude", allEntries = true)
    public LogSaudeResponseDTO atualizar(Long id, LogSaudeRequestDTO dto) {
        LogSaude log = buscarEntidade(id);
        Pet pet = petRepository.findById(dto.getIdPet())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado: " + dto.getIdPet()));

        log.setPet(pet);
        log.setDtRegistro(dto.getDtRegistro());
        log.setVlMetrica(dto.getVlMetrica());
        log.setMetrica(dto.getMetrica());
        log.setObs(dto.getObs());

        return LogSaudeResponseDTO.from(logSaudeRepository.save(log));
    }

    @Transactional
    @CacheEvict(value = "logsSaude", allEntries = true)
    public void excluir(Long id) {
        if (!logSaudeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Log de saúde não encontrado: " + id);
        }
        logSaudeRepository.deleteById(id);
    }

    private LogSaude buscarEntidade(Long id) {
        return logSaudeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Log de saúde não encontrado: " + id));
    }
}
