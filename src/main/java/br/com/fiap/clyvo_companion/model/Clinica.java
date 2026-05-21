package br.com.fiap.clyvo_companion.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_CC_CLINICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clinica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_clinica")
    @SequenceGenerator(name = "seq_clinica", sequenceName = "SQ_CC_CLINICA", allocationSize = 1)
    @Column(name = "ID_CLINICA")
    private Long idClinica;

    @Column(name = "NOME_CLINICA", nullable = false, length = 100)
    private String nomeClinica;

    @Column(name = "CNPJ", nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(name = "ENDERECO", nullable = false, length = 200)
    private String endereco;

    @Column(name = "TELEFONE", length = 15)
    private String telefone;

    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Agendamento> agendamentos = new ArrayList<>();
}
