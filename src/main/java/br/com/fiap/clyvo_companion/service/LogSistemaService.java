package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.dto.LogSistemaRequestDTO;
import br.com.fiap.clyvo_companion.dto.LogSistemaResponseDTO;
import br.com.fiap.clyvo_companion.exception.ResourceNotFoundException;
import br.com.fiap.clyvo_companion.model.LogSistema;
import br.com.fiap.clyvo_companion.repository.LogSistemaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogSistemaService {

    private final LogSistemaRepository logSistemaRepository;

    public LogSistemaService(LogSistemaRepository logSistemaRepository) {
        this.logSistemaRepository = logSistemaRepository;
    }

    @Transactional(readOnly = true)
    public LogSistemaResponseDTO buscarPorId(Long id) {
        return LogSistemaResponseDTO.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Page<LogSistemaResponseDTO> listar(String nomeProc, Integer cdErro, Pageable pageable) {
        return logSistemaRepository.buscarComFiltros(nomeProc, cdErro, pageable)
                .map(LogSistemaResponseDTO::from);
    }

    @Transactional
    public LogSistemaResponseDTO criar(LogSistemaRequestDTO dto) {
        LogSistema log = LogSistema.builder()
                .nomeProc(dto.getNomeProc())
                .nomeUsuario(dto.getNomeUsuario())
                .dtErro(dto.getDtErro())
                .cdErro(dto.getCdErro())
                .msgErro(dto.getMsgErro())
                .build();

        return LogSistemaResponseDTO.from(logSistemaRepository.save(log));
    }

    @Transactional
    public void excluir(Long id) {
        if (!logSistemaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Log de sistema não encontrado: " + id);
        }
        logSistemaRepository.deleteById(id);
    }

    private LogSistema buscarEntidade(Long id) {
        return logSistemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Log de sistema não encontrado: " + id));
    }
}
