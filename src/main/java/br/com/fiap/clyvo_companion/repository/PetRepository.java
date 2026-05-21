package br.com.fiap.clyvo_companion.repository;

import br.com.fiap.clyvo_companion.model.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Page<Pet> findByUsuarioIdUsuario(Long idUsuario, Pageable pageable);

    @Query("""
            SELECT p FROM Pet p
            WHERE (:nome IS NULL OR LOWER(p.nomePet) LIKE LOWER(CONCAT('%', :nome, '%')))
              AND (:especie IS NULL OR LOWER(p.especie) = LOWER(:especie))
              AND (:idUsuario IS NULL OR p.usuario.idUsuario = :idUsuario)
            """)
    Page<Pet> buscarComFiltros(
            @Param("nome") String nome,
            @Param("especie") String especie,
            @Param("idUsuario") Long idUsuario,
            Pageable pageable);
}
