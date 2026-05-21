package br.com.fiap.clyvo_companion.repository;

import br.com.fiap.clyvo_companion.model.Agendamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("""
            SELECT a FROM Agendamento a
            WHERE a.pet.idPet = :idPet
              AND a.dtAgenda >= :agora
              AND a.status IN ('AGENDADO', 'CONFIRMADO')
            ORDER BY a.dtAgenda ASC
            """)
    List<Agendamento> findProximosPorPet(@Param("idPet") Long idPet, @Param("agora") LocalDateTime agora);

    @Query("""
            SELECT a FROM Agendamento a
            WHERE (:idPet IS NULL OR a.pet.idPet = :idPet)
              AND (:idClinica IS NULL OR a.clinica.idClinica = :idClinica)
              AND (:status IS NULL OR a.status = :status)
            """)
    Page<Agendamento> buscarComFiltros(
            @Param("idPet") Long idPet,
            @Param("idClinica") Long idClinica,
            @Param("status") String status,
            Pageable pageable);
}
