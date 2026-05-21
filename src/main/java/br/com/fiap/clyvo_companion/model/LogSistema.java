package br.com.fiap.clyvo_companion.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "TB_CC_LOG_SISTEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_log_sistema")
    @SequenceGenerator(name = "seq_log_sistema", sequenceName = "SQ_CC_LOG_SISTEMA", allocationSize = 1)
    @Column(name = "ID_ERRO")
    private Long idErro;

    @Column(name = "NOME_PROC", nullable = false, length = 100)
    private String nomeProc;

    @Column(name = "NOME_USUARIO", length = 100)
    private String nomeUsuario;

    @Column(name = "DT_ERRO", nullable = false)
    private LocalDate dtErro;

    @Column(name = "CD_ERRO", nullable = false)
    private Integer cdErro;

    @Column(name = "MSG_ERRO", nullable = false, length = 400)
    private String msgErro;
}
