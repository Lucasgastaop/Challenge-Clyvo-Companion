package br.com.fiap.clyvo_companion.repository;

import br.com.fiap.clyvo_companion.model.Clinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicaRepository extends JpaRepository<Clinica, Long> {

    Optional<Clinica> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);
}
