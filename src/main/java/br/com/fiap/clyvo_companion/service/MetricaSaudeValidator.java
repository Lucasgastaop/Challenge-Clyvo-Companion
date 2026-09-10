package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Limites físicos das métricas de saúde. Valores fora da faixa são rejeitados antes da persistência.
 */
@Component
public class MetricaSaudeValidator {

    private static final Map<String, Limite> LIMITES = new LinkedHashMap<>();

    static {
        LIMITES.put("peso", new Limite("Peso", new BigDecimal("0.10"), new BigDecimal("120.00"), "kg"));
        LIMITES.put("alimentacao", new Limite("Alimentação", new BigDecimal("1"), new BigDecimal("5000"), "g"));
        LIMITES.put("exercicio", new Limite("Exercício", BigDecimal.ZERO, new BigDecimal("480"), "min"));
        LIMITES.put("temperatura", new Limite("Temperatura", new BigDecimal("30.00"), new BigDecimal("45.00"), "°C"));
        LIMITES.put("frequencia cardiaca", new Limite("Frequência cardíaca", new BigDecimal("30"), new BigDecimal("300"), "bpm"));
    }

    public void validar(String metrica, BigDecimal valor) {
        if (metrica == null || metrica.isBlank() || valor == null) {
            throw new BusinessRuleException("Métrica e valor são obrigatórios");
        }

        String chave = normalizar(metrica);
        Limite limite = LIMITES.get(chave);
        if (limite == null) {
            throw new BusinessRuleException(
                    "Métrica não suportada: " + metrica + ". Use: " + String.join(", ", LIMITES.keySet()));
        }
        if (valor.compareTo(limite.min()) < 0 || valor.compareTo(limite.max()) > 0) {
            throw new BusinessRuleException(
                    "Valor de " + chave + " fora dos limites ("
                            + limite.min() + " a " + limite.max() + " " + limite.unidade() + ")");
        }
    }

    public Set<String> metricasPermitidas() {
        return LIMITES.keySet();
    }

    public Map<String, String> opcoesFormulario() {
        Map<String, String> opcoes = new LinkedHashMap<>();
        LIMITES.forEach((chave, limite) -> opcoes.put(chave, limite.rotuloFormulario()));
        return opcoes;
    }

    public String normalizar(String metrica) {
        return metrica.toLowerCase().trim().replace('_', ' ');
    }

    private record Limite(String rotulo, BigDecimal min, BigDecimal max, String unidade) {
        String rotuloFormulario() {
            return rotulo + " (" + unidade + ") — " + formatar(min) + " a " + formatar(max);
        }

        private static String formatar(BigDecimal valor) {
            return valor.stripTrailingZeros().toPlainString().replace('.', ',');
        }
    }
}
