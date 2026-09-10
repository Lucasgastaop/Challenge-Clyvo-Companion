package br.com.fiap.clyvo_companion.controller.web;

import br.com.fiap.clyvo_companion.exception.BusinessRuleException;
import br.com.fiap.clyvo_companion.exception.ResourceNotFoundException;
import org.springframework.validation.BindingResult;

/**
 * Fluxo comum dos formulários Thymeleaf: Bean Validation + regras de negócio.
 */
final class WebFormSupport {

    private WebFormSupport() {
    }

    static boolean falhouAoSalvar(BindingResult bindingResult, Runnable persistir) {
        if (bindingResult.hasErrors()) {
            return true;
        }
        try {
            persistir.run();
            return false;
        } catch (BusinessRuleException | ResourceNotFoundException ex) {
            bindingResult.reject("negocio", ex.getMessage());
            return true;
        }
    }
}
