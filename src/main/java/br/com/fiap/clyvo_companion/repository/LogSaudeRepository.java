package br.com.fiap.clyvo_companion.repository;

import br.com.fiap.clyvo_companion.model.LogSaude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogSaudeRepository extends JpaRepository<LogSaude, Long> {

    List<LogSaude> findByPetIdPetOrderByDtRegistroDesc(Long idPet);
}
