package br.com.fiap.clyvo_companion.repository;

import br.com.fiap.clyvo_companion.model.Clinica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicaRepository extends JpaRepository<Clinica, Long> {

    Optional<Clinica> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);

    @Query("""
            SELECT c FROM Clinica c
            WHERE (:nome IS NULL OR LOWER(c.nomeClinica) LIKE LOWER(CONCAT('%', :nome, '%')))
              AND (:cnpj IS NULL OR c.cnpj = :cnpj)
            """)
    Page<Clinica> buscarComFiltros(
            @Param("nome") String nome,
            @Param("cnpj") String cnpj,
            Pageable pageable);
}
