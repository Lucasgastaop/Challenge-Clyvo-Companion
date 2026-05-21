package br.com.fiap.clyvo_companion.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CC_LOG_SAUDE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_log_saude")
    @SequenceGenerator(name = "seq_log_saude", sequenceName = "SQ_CC_LOG_SAUDE", allocationSize = 1)
    @Column(name = "ID_LOG")
    private Long idLog;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @Column(name = "DT_REGISTRO", nullable = false)
    private LocalDateTime dtRegistro;

    @Column(name = "VL_METRICA", nullable = false, precision = 10, scale = 2)
    private BigDecimal vlMetrica;

    @Column(name = "METRICA", nullable = false, length = 30)
    private String metrica;

    @Column(name = "OBS", length = 255)
    private String obs;
}
