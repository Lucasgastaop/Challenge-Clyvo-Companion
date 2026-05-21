package br.com.fiap.clyvo_companion.repository;

import br.com.fiap.clyvo_companion.model.LogSaude;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogSaudeRepository extends JpaRepository<LogSaude, Long> {

    List<LogSaude> findTop5ByPetIdPetOrderByDtRegistroDesc(Long idPet);

    @Query("""
            SELECT l FROM LogSaude l
            WHERE (:idPet IS NULL OR l.pet.idPet = :idPet)
              AND (
                LOWER(l.metrica) = 'temperatura' AND (l.vlMetrica < 37 OR l.vlMetrica > 39)
                OR LOWER(l.metrica) IN ('frequencia cardiaca', 'frequencia_cardiaca')
                    AND (l.vlMetrica < 60 OR l.vlMetrica > 180)
                OR LOWER(l.metrica) = 'peso' AND l.vlMetrica < 0.5
              )
            """)
    Page<LogSaude> buscarAlertas(@Param("idPet") Long idPet, Pageable pageable);

    @Query("""
            SELECT l FROM LogSaude l
            WHERE (:idPet IS NULL OR l.pet.idPet = :idPet)
              AND (:metrica IS NULL OR LOWER(l.metrica) = LOWER(:metrica))
            """)
    Page<LogSaude> buscarComFiltros(
            @Param("idPet") Long idPet,
            @Param("metrica") String metrica,
            Pageable pageable);
}
