package br.com.fiap.clyvo_companion.model.enums;

/**
 * Valores alinhados à constraint CK_STATUS_AGENDAMENTO do Oracle (TB_CC_AGENDAMENTO).
 */
public enum StatusAgendamento {

    AGENDADO,
    CANCELADO,
    CONCLUIDO;

    public static StatusAgendamento fromValor(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Status não informado");
        }
        try {
            return StatusAgendamento.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Status inválido: " + valor + ". Valores aceitos: AGENDADO, CANCELADO, CONCLUIDO");
        }
    }
}
