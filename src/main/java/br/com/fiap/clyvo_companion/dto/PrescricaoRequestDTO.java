package br.com.fiap.clyvo_companion.dto;

import br.com.fiap.clyvo_companion.service.PeriodoPrescricao;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescricaoRequestDTO {

    public static final int FREQUENCIA_PADRAO_HORAS = 24;

    @NotNull(message = "Selecione o pet")
    private Long idPet;

    @NotBlank(message = "Informe o medicamento")
    @Size(max = 100, message = "O medicamento deve ter no máximo 100 caracteres")
    private String nomeMedicamento;

    @NotBlank(message = "Informe a dosagem")
    @Size(max = 50, message = "A dosagem deve ter no máximo 50 caracteres")
    private String dsDosagem;

    @NotNull(message = "Informe a frequência em horas")
    @Min(value = 1, message = "A frequência mínima é de 1 hora")
    @Max(value = 72, message = "A frequência máxima é de 72 horas")
    private Integer frequenciaHoras = FREQUENCIA_PADRAO_HORAS;

    @NotNull(message = "Informe a data de início")
    private LocalDate dtInicio;

    private LocalDate dtFim;

    @AssertTrue(message = PeriodoPrescricao.MENSAGEM_INVALIDO)
    public boolean isPeriodoValido() {
        return PeriodoPrescricao.isValido(dtInicio, dtFim);
    }
}
