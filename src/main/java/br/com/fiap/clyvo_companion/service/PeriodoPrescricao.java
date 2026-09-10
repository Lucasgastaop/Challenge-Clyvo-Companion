package br.com.fiap.clyvo_companion.service;

import java.time.LocalDate;

/**
 * Regra única do período da prescrição: término ausente ou igual/posterior ao início.
 */
public final class PeriodoPrescricao {

    public static final String MENSAGEM_INVALIDO =
            "A data de término deve ser igual ou posterior à data de início";

    private PeriodoPrescricao() {
    }

    public static boolean isValido(LocalDate dtInicio, LocalDate dtFim) {
        return dtFim == null || dtInicio == null || !dtFim.isBefore(dtInicio);
    }
}
