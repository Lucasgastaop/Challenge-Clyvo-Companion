package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.dto.LogSaudeAlertaDTO;
import br.com.fiap.clyvo_companion.dto.LogSaudeRequestDTO;
import br.com.fiap.clyvo_companion.dto.LogSaudeResponseDTO;
import br.com.fiap.clyvo_companion.exception.ResourceNotFoundException;
import br.com.fiap.clyvo_companion.model.LogSaude;
import br.com.fiap.clyvo_companion.model.Pet;
import br.com.fiap.clyvo_companion.repository.LogSaudeRepository;
import br.com.fiap.clyvo_companion.repository.PetRepository;
import br.com.fiap.clyvo_companion.security.PetAcessoPolicy;
import br.com.fiap.clyvo_companion.security.UsuarioAutenticadoService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogSaudeService {

    private final LogSaudeRepository logSaudeRepository;
    private final PetRepository petRepository;
    private final LogSaudeAlertaAnalyzer alertaAnalyzer;
    private final MetricaSaudeValidator metricaSaudeValidator;
    private final PetAcessoPolicy petAcessoPolicy;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public LogSaudeService(
            LogSaudeRepository logSaudeRepository,
            PetRepository petRepository,
            LogSaudeAlertaAnalyzer alertaAnalyzer,
            MetricaSaudeValidator metricaSaudeValidator,
            PetAcessoPolicy petAcessoPolicy,
            UsuarioAutenticadoService usuarioAutenticadoService) {
        this.logSaudeRepository = logSaudeRepository;
        this.petRepository = petRepository;
        this.alertaAnalyzer = alertaAnalyzer;
        this.metricaSaudeValidator = metricaSaudeValidator;
        this.petAcessoPolicy = petAcessoPolicy;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
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

    @Transactional(readOnly = true)
    public List<LogSaudeResponseDTO> listarDoTutor(Long idUsuario) {
        return logSaudeRepository.findByTutorComPet(idUsuario).stream()
                .map(LogSaudeResponseDTO::from)
                .toList();
    }

    /**
     * Retorna logs com métricas fora dos limites de referência (temperatura, frequência, peso).
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "logsSaudeAlertas", key = "#idPet + '-' + #pageable.pageNumber")
    public Page<LogSaudeAlertaDTO> listarAlertas(Long idPet, Pageable pageable) {
        if (idPet != null && !petRepository.existsById(idPet)) {
            throw new ResourceNotFoundException("Pet não encontrado: " + idPet);
        }

        return logSaudeRepository.buscarAlertas(idPet, pageable)
                .map(log -> alertaAnalyzer.analisar(log)
                        .orElseThrow(() -> new IllegalStateException("Log sem alerta: " + log.getIdLog())));
    }

    @Transactional
    @CacheEvict(value = {"logsSaude", "logsSaudeAlertas", "petsResumo"}, allEntries = true)
    public LogSaudeResponseDTO criar(LogSaudeRequestDTO dto) {
        LogSaude log = LogSaude.builder()
                .pet(prepararPetValidado(dto))
                .dtRegistro(dto.getDtRegistro() != null ? dto.getDtRegistro() : LocalDateTime.now())
                .vlMetrica(dto.getVlMetrica())
                .metrica(metricaSaudeValidator.normalizar(dto.getMetrica()))
                .obs(dto.getObs())
                .build();

        return LogSaudeResponseDTO.from(logSaudeRepository.save(log));
    }

    @Transactional
    @CacheEvict(value = {"logsSaude", "logsSaudeAlertas", "petsResumo"}, allEntries = true)
    public LogSaudeResponseDTO atualizar(Long id, LogSaudeRequestDTO dto) {
        LogSaude log = buscarEntidade(id);
        log.setPet(prepararPetValidado(dto));
        log.setDtRegistro(dto.getDtRegistro());
        log.setVlMetrica(dto.getVlMetrica());
        log.setMetrica(metricaSaudeValidator.normalizar(dto.getMetrica()));
        log.setObs(dto.getObs());

        return LogSaudeResponseDTO.from(logSaudeRepository.save(log));
    }

    @Transactional
    @CacheEvict(value = {"logsSaude", "logsSaudeAlertas", "petsResumo"}, allEntries = true)
    public void excluir(Long id) {
        if (!logSaudeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Log de saúde não encontrado: " + id);
        }
        logSaudeRepository.deleteById(id);
    }

    private Pet prepararPetValidado(LogSaudeRequestDTO dto) {
        Pet pet = buscarPet(dto.getIdPet());
        usuarioAutenticadoService.getUsuarioLogado()
                .ifPresent(usuario -> petAcessoPolicy.garantirPetDoTutor(pet, usuario));
        metricaSaudeValidator.validar(dto.getMetrica(), dto.getVlMetrica());
        return pet;
    }

    private LogSaude buscarEntidade(Long id) {
        return logSaudeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Log de saúde não encontrado: " + id));
    }

    private Pet buscarPet(Long idPet) {
        return petRepository.findById(idPet)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado: " + idPet));
    }
}
