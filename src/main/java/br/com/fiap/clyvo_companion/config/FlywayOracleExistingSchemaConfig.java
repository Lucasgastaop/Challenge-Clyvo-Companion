package br.com.fiap.clyvo_companion.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationState;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * No Oracle da FIAP o schema já tem as tabelas do Clyvo. Se o histórico do Flyway
 * não tiver a V1 aplicada, ele é recriado no baseline 1 para não repetir o CREATE.
 */
@Configuration
@Profile("oracle")
public class FlywayOracleExistingSchemaConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            if (tabelasClyvoExistem(flyway) && !migracaoV1Concluida(flyway)) {
                droparHistoricoSeExistir(flyway);
                Flyway.configure()
                        .configuration(flyway.getConfiguration())
                        .baselineOnMigrate(true)
                        .baselineVersion("1")
                        .load()
                        .migrate();
                return;
            }
            flyway.repair();
            flyway.migrate();
        };
    }

    private boolean tabelasClyvoExistem(Flyway flyway) {
        return nomeTabela(flyway, "TB_CC_USUARIO") != null;
    }

    private boolean migracaoV1Concluida(Flyway flyway) {
        try {
            for (MigrationInfo info : flyway.info().all()) {
                if (info.getVersion() == null || !"1".equals(info.getVersion().getVersion())) {
                    continue;
                }
                MigrationState state = info.getState();
                return state == MigrationState.SUCCESS || state == MigrationState.BASELINE;
            }
            return false;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private void droparHistoricoSeExistir(Flyway flyway) {
        String historico = nomeTabela(flyway, "FLYWAY_SCHEMA_HISTORY");
        if (historico == null) {
            return;
        }
        try (Connection connection = flyway.getConfiguration().getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE \"" + historico + "\" PURGE");
        } catch (SQLException e) {
            if (e.getErrorCode() != 942) {
                throw new IllegalStateException("Não foi possível recriar o histórico do Flyway no Oracle", e);
            }
        }
    }

    private String nomeTabela(Flyway flyway, String nomeLogico) {
        try (Connection connection = flyway.getConfiguration().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT table_name FROM user_tables WHERE UPPER(table_name) = ?")) {
            statement.setString(1, nomeLogico.toUpperCase());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(1);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao consultar o schema Oracle do Clyvo: " + e.getMessage(), e);
        }
    }
}
