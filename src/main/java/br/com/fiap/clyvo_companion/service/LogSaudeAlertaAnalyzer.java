package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.dto.LogSaudeAlertaDTO;
import br.com.fiap.clyvo_companion.model.LogSaude;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Analisa métricas de saúde e identifica valores fora dos limites de referência.
 */
@Component
public class LogSaudeAlertaAnalyzer {

    private static final BigDecimal TEMP_MIN = new BigDecimal("37.0");
    private static final BigDecimal TEMP_MAX = new BigDecimal("39.0");
    private static final BigDecimal FREQ_MIN = new BigDecimal("60");
    private static final BigDecimal FREQ_MAX = new BigDecimal("180");
    private static final BigDecimal PESO_MIN = new BigDecimal("0.5");

    public Optional<LogSaudeAlertaDTO> analisar(LogSaude log) {
        String metrica = log.getMetrica().toLowerCase().trim();
        BigDecimal valor = log.getVlMetrica();

        Optional<AlertaInfo> alerta = switch (metrica) {
            case "temperatura" -> analisarTemperatura(valor);
            case "frequencia cardiaca", "frequencia_cardiaca" -> analisarFrequencia(valor);
            case "peso" -> analisarPeso(valor);
            default -> Optional.empty();
        };

        return alerta.map(info -> montarDto(log, info));
    }

    private Optional<AlertaInfo> analisarTemperatura(BigDecimal valor) {
        if (valor.compareTo(TEMP_MIN) < 0) {
            return Optional.of(new AlertaInfo("Temperatura abaixo do normal (< 37°C)", "ALTO"));
        }
        if (valor.compareTo(TEMP_MAX) > 0) {
            return Optional.of(new AlertaInfo("Temperatura acima do normal (> 39°C)", "ALTO"));
        }
        return Optional.empty();
    }

    private Optional<AlertaInfo> analisarFrequencia(BigDecimal valor) {
        if (valor.compareTo(FREQ_MIN) < 0) {
            return Optional.of(new AlertaInfo("Frequência cardíaca abaixo do normal (< 60 bpm)", "ALTO"));
        }
        if (valor.compareTo(FREQ_MAX) > 0) {
            return Optional.of(new AlertaInfo("Frequência cardíaca acima do normal (> 180 bpm)", "ALTO"));
        }
        return Optional.empty();
    }

    private Optional<AlertaInfo> analisarPeso(BigDecimal valor) {
        if (valor.compareTo(PESO_MIN) < 0) {
            return Optional.of(new AlertaInfo("Peso abaixo do mínimo de referência (< 0,5 kg)", "MEDIO"));
        }
        return Optional.empty();
    }

    private LogSaudeAlertaDTO montarDto(LogSaude log, AlertaInfo info) {
        LogSaudeAlertaDTO dto = new LogSaudeAlertaDTO();
        dto.setIdLog(log.getIdLog());
        dto.setIdPet(log.getPet().getIdPet());
        dto.setDtRegistro(log.getDtRegistro());
        dto.setVlMetrica(log.getVlMetrica());
        dto.setMetrica(log.getMetrica());
        dto.setObs(log.getObs());
        dto.setMotivoAlerta(info.motivo());
        dto.setNivelAlerta(info.nivel());
        return dto;
    }

    private static class AlertaInfo {

        private final String motivo;
        private final String nivel;

        AlertaInfo(String motivo, String nivel) {
            this.motivo = motivo;
            this.nivel = nivel;
        }

        String motivo() {
            return motivo;
        }

        String nivel() {
            return nivel;
        }
    }
}
