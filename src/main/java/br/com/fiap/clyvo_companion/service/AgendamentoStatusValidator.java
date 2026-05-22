package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.exception.BusinessRuleException;
import br.com.fiap.clyvo_companion.model.enums.StatusAgendamento;
import org.springframework.stereotype.Component;

@Component
public class AgendamentoStatusValidator {

    public void validarTransicao(StatusAgendamento atual, StatusAgendamento novo) {
        if (atual == novo) {
            return;
        }

        if (atual == StatusAgendamento.CANCELADO || atual == StatusAgendamento.CONCLUIDO) {
            throw new BusinessRuleException(
                    "Agendamento com status " + atual + " não pode ser alterado");
        }

        boolean transicaoValida = switch (atual) {
            case AGENDADO -> novo == StatusAgendamento.CONCLUIDO || novo == StatusAgendamento.CANCELADO;
            default -> false;
        };

        if (!transicaoValida) {
            throw new BusinessRuleException(
                    "Transição inválida de " + atual + " para " + novo);
        }
    }
}
