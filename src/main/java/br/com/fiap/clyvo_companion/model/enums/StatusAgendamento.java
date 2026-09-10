package br.com.fiap.clyvo_companion.model.enums;

/**
 * Valores alinhados à constraint CK_STATUS_AGENDAMENTO do Oracle (TB_CC_AGENDAMENTO).
 */
public enum StatusAgendamento {

    AGENDADO,
    CANCELADO,
    CONCLUIDO;

    public static StatusAgendamento fromValor(String valor) {
        return ValorEnum.fromValor(StatusAgendamento.class, valor, "Status");
    }
}
