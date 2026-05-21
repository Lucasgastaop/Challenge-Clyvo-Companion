package br.com.fiap.clyvo_companion.repository;

import br.com.fiap.clyvo_companion.model.Prescricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescricaoRepository extends JpaRepository<Prescricao, Long> {

    List<Prescricao> findByPetIdPet(Long idPet);
}
