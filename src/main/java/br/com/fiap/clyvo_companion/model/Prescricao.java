package br.com.fiap.clyvo_companion.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "TB_CC_PRESCRICAO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescricao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_prescricao")
    @SequenceGenerator(name = "seq_prescricao", sequenceName = "SQ_CC_PRESCRICAO", allocationSize = 1)
    @Column(name = "ID_PRESCRICAO")
    private Long idPrescricao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @Column(name = "NOME_MEDICAMENTO", nullable = false, length = 100)
    private String nomeMedicamento;

    @Column(name = "DS_DOSAGEM", nullable = false, length = 50)
    private String dsDosagem;

    @Column(name = "FREQUENCIA_HORAS", nullable = false)
    private Integer frequenciaHoras;

    @Column(name = "DT_INICIO", nullable = false)
    private LocalDate dtInicio;

    @Column(name = "DT_FIM")
    private LocalDate dtFim;
}
