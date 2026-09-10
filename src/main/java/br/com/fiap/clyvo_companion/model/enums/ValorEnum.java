package br.com.fiap.clyvo_companion.model.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Conversão compartilhada de texto para enum, com mensagem de erro padronizada.
 */
public final class ValorEnum {

    private ValorEnum() {
    }

    public static <E extends Enum<E>> E fromValor(Class<E> tipo, String valor, String rotulo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(rotulo + " não informado");
        }
        try {
            return Enum.valueOf(tipo, valor.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            String aceitos = Arrays.stream(tipo.getEnumConstants())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(rotulo + " inválido: " + valor + ". Valores aceitos: " + aceitos);
        }
    }
}
