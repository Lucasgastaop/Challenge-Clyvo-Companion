package br.com.fiap.clyvo_companion.repository;

import br.com.fiap.clyvo_companion.model.LogSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogSistemaRepository extends JpaRepository<LogSistema, Long> {
}
