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
        if (valor.compareTo(LimitesAlertaSaude.TEMPERATURA_MIN) < 0) {
            return Optional.of(new AlertaInfo(
                    "Temperatura abaixo do normal (< " + LimitesAlertaSaude.formatar(LimitesAlertaSaude.TEMPERATURA_MIN) + "°C)",
                    "ALTO"));
        }
        if (valor.compareTo(LimitesAlertaSaude.TEMPERATURA_MAX) > 0) {
            return Optional.of(new AlertaInfo(
                    "Temperatura acima do normal (> " + LimitesAlertaSaude.formatar(LimitesAlertaSaude.TEMPERATURA_MAX) + "°C)",
                    "ALTO"));
        }
        return Optional.empty();
    }

    private Optional<AlertaInfo> analisarFrequencia(BigDecimal valor) {
        if (valor.compareTo(LimitesAlertaSaude.FREQUENCIA_MIN) < 0) {
            return Optional.of(new AlertaInfo(
                    "Frequência cardíaca abaixo do normal (< " + LimitesAlertaSaude.formatar(LimitesAlertaSaude.FREQUENCIA_MIN) + " bpm)",
                    "ALTO"));
        }
        if (valor.compareTo(LimitesAlertaSaude.FREQUENCIA_MAX) > 0) {
            return Optional.of(new AlertaInfo(
                    "Frequência cardíaca acima do normal (> " + LimitesAlertaSaude.formatar(LimitesAlertaSaude.FREQUENCIA_MAX) + " bpm)",
                    "ALTO"));
        }
        return Optional.empty();
    }

    private Optional<AlertaInfo> analisarPeso(BigDecimal valor) {
        if (valor.compareTo(LimitesAlertaSaude.PESO_MIN) < 0) {
            return Optional.of(new AlertaInfo(
                    "Peso abaixo do mínimo de referência (< " + LimitesAlertaSaude.formatar(LimitesAlertaSaude.PESO_MIN) + " kg)",
                    "MEDIO"));
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

    private record AlertaInfo(String motivo, String nivel) {
    }
}
