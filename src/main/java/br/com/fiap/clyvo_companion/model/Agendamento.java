package br.com.fiap.clyvo_companion.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CC_AGENDAMENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_agendamento")
    @SequenceGenerator(name = "seq_agendamento", sequenceName = "SQ_CC_AGENDAMENTO", allocationSize = 1)
    @Column(name = "ID_AGENDAMENTO")
    private Long idAgendamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CLINICA", nullable = false)
    private Clinica clinica;

    @Column(name = "DT_AGENDA", nullable = false)
    private LocalDateTime dtAgenda;

    @Column(name = "TIPO_SERVICO", nullable = false, length = 50)
    private String tipoServico;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status;
}
