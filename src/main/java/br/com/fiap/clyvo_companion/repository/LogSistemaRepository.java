package br.com.fiap.clyvo_companion.repository;

import br.com.fiap.clyvo_companion.model.LogSistema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LogSistemaRepository extends JpaRepository<LogSistema, Long> {

    @Query("""
            SELECT l FROM LogSistema l
            WHERE (:nomeProc IS NULL OR LOWER(l.nomeProc) LIKE LOWER(CONCAT('%', :nomeProc, '%')))
              AND (:cdErro IS NULL OR l.cdErro = :cdErro)
            """)
    Page<LogSistema> buscarComFiltros(
            @Param("nomeProc") String nomeProc,
            @Param("cdErro") Integer cdErro,
            Pageable pageable);
}
