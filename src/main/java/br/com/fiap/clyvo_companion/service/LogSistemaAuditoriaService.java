package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.model.LogSistema;
import br.com.fiap.clyvo_companion.repository.LogSistemaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Persiste erros da API em TB_CC_LOG_SISTEMA (auditoria automática).
 */
@Service
public class LogSistemaAuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(LogSistemaAuditoriaService.class);
    private static final int MSG_MAX = 400;
    private static final int PROC_MAX = 100;
    private static final String HEADER_USUARIO = "X-Usuario";

    private final LogSistemaRepository logSistemaRepository;

    public LogSistemaAuditoriaService(LogSistemaRepository logSistemaRepository) {
        this.logSistemaRepository = logSistemaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(HttpServletRequest request, HttpStatus status, Exception ex) {
        try {
            LogSistema logSistema = LogSistema.builder()
                    .nomeProc(montarNomeProc(request))
                    .nomeUsuario(resolverUsuario(request))
                    .dtErro(LocalDate.now())
                    .cdErro(status.value())
                    .msgErro(truncar(montarMensagem(status, ex), MSG_MAX))
                    .build();

            logSistemaRepository.save(logSistema);
        } catch (Exception e) {
            log.error("Falha ao gravar log de sistema: {}", e.getMessage());
        }
    }

    private String montarNomeProc(HttpServletRequest request) {
        String proc = request.getMethod() + " " + request.getRequestURI();
        return truncar(proc, PROC_MAX);
    }

    private String resolverUsuario(HttpServletRequest request) {
        String usuario = request.getHeader(HEADER_USUARIO);
        if (usuario != null && !usuario.isBlank()) {
            return truncar(usuario.trim(), 100);
        }
        return "API";
    }

    private String montarMensagem(HttpStatus status, Exception ex) {
        String tipo = ex.getClass().getSimpleName();
        String msg = ex.getMessage() != null ? ex.getMessage() : status.getReasonPhrase();
        return tipo + ": " + msg;
    }

    private String truncar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= max ? texto : texto.substring(0, max);
    }
}
