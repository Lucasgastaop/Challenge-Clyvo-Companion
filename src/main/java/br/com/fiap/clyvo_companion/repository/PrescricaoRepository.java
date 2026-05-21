package br.com.fiap.clyvo_companion.repository;

import br.com.fiap.clyvo_companion.model.Prescricao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrescricaoRepository extends JpaRepository<Prescricao, Long> {

    @Query("""
            SELECT p FROM Prescricao p
            WHERE p.pet.idPet = :idPet
              AND (p.dtFim IS NULL OR p.dtFim >= :hoje)
            ORDER BY p.dtInicio DESC
            """)
    List<Prescricao> findAtivasPorPet(@Param("idPet") Long idPet, @Param("hoje") LocalDate hoje);

    @Query("""
            SELECT p FROM Prescricao p
            WHERE (:idPet IS NULL OR p.pet.idPet = :idPet)
              AND (:medicamento IS NULL OR LOWER(p.nomeMedicamento) LIKE LOWER(CONCAT('%', :medicamento, '%')))
            """)
    Page<Prescricao> buscarComFiltros(
            @Param("idPet") Long idPet,
            @Param("medicamento") String medicamento,
            Pageable pageable);
}
