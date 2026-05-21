package br.com.fiap.clyvo_companion.repository;

import br.com.fiap.clyvo_companion.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByPetIdPet(Long idPet);

    List<Agendamento> findByClinicaIdClinica(Long idClinica);
}
