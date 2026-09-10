package br.com.fiap.clyvo_companion.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Única fonte dos limiares de alerta. Usada pelo analyzer e pela consulta JPQL (SpEL).
 */
@Component("limitesAlertaSaude")
public class LimitesAlertaSaude {

    public static final BigDecimal TEMPERATURA_MIN = new BigDecimal("37.0");
    public static final BigDecimal TEMPERATURA_MAX = new BigDecimal("39.0");
    public static final BigDecimal FREQUENCIA_MIN = new BigDecimal("60");
    public static final BigDecimal FREQUENCIA_MAX = new BigDecimal("180");
    public static final BigDecimal PESO_MIN = new BigDecimal("0.5");

    public BigDecimal getTemperaturaMin() {
        return TEMPERATURA_MIN;
    }

    public BigDecimal getTemperaturaMax() {
        return TEMPERATURA_MAX;
    }

    public BigDecimal getFrequenciaMin() {
        return FREQUENCIA_MIN;
    }

    public BigDecimal getFrequenciaMax() {
        return FREQUENCIA_MAX;
    }

    public BigDecimal getPesoMin() {
        return PESO_MIN;
    }

    public static String formatar(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString().replace('.', ',');
    }
}
