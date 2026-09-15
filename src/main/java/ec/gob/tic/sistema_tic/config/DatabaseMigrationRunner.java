package ec.gob.tic.sistema_tic.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Migraciones ligeras que se aplican automáticamente al iniciar la aplicación,
 * sin depender de Flyway/Liquibase ni de ejecutar scripts a mano en cada
 * ambiente (local, Render, Supabase, etc.).
 *
 * <p>Todas las migraciones deben ser idempotentes y seguras: si ya se
 * aplicaron, no hacen nada.
 */
@Component
@ConditionalOnProperty(name = "app.migraciones.auto", havingValue = "true", matchIfMissing = true)
public class DatabaseMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationRunner.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        eliminarCheckTipoMantenimiento();
    }

    /**
     * V4: Elimina cualquier constraint CHECK sobre la columna
     * {@code tipo_mantenimiento} de la tabla {@code mantenimiento}.
     *
     * <p>Contexto: la columna tenía una restricción que únicamente aceptaba
     * 'PREVENTIVO' y 'CORRECTIVO' (en mayúsculas), pero el sistema registra el
     * tipo de mantenimiento desde el catálogo TIPO_MANTENIMIENTO, que contiene
     * valores como "Preventivo", "Correctivo" y "Predictivo". Al intentar crear
     * un mantenimiento se producía el error:
     * "El dato que intenta registrar ya existe o viola una restricción de la
     * base de datos." (HTTP 409).
     */
    private void eliminarCheckTipoMantenimiento() {
        try {
            List<String> constraints = jdbcTemplate.queryForList(
                    """
                            SELECT conname
                            FROM pg_constraint
                            WHERE conrelid = 'mantenimiento'::regclass
                              AND contype = 'c'
                              AND pg_get_constraintdef(oid) ILIKE '%tipo_mantenimiento%'
                            ORDER BY conname
                            """,
                    String.class
            );

            if (constraints.isEmpty()) {
                log.info("Migración V4: no existe ninguna restricción CHECK sobre tipo_mantenimiento. Nada que hacer.");
                return;
            }

            for (String nombre : constraints) {
                String sql = "ALTER TABLE mantenimiento DROP CONSTRAINT \"" +
                        nombre.replace("\"", "\"\"") + "\"";
                jdbcTemplate.execute(sql);
                log.info("Migración V4: restricción CHECK '{}' eliminada de la tabla mantenimiento.", nombre);
            }

        } catch (Exception ex) {
            log.error("Migración V4: no se pudo eliminar la restricción CHECK sobre tipo_mantenimiento.", ex);
        }
    }
}