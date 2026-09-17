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
 * <p>El perfil de producción usa {@code spring.jpa.hibernate.ddl-auto=update},
 * pero Hibernate NO puede agregar columnas {@code NOT NULL} sobre tablas que ya
 * contienen filas: la sentencia {@code ALTER TABLE ... ADD COLUMN ... NOT NULL}
 * falla, Hibernate lo registra y continúa, y luego las consultas que referencia
 * esas columnas terminan en error 500 ("column X does not exist").
 *
 * <p>Por eso aquí se replican las migraciones V2, V3 y V4 de forma idempotente
 * y resiliente: cada sentencia se ejecuta por separado y un fallo no detiene las
 * demás. Si ya están aplicadas, no hacen nada.
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
        ejecutarSentenciasMigracionV2V3();
        eliminarCheckTipoMantenimiento();
    }

    /**
     * V2 y V3: garantiza que existan las columnas que exigen las entidades JPA
     * y que no tengan valores NULL, para que las consultas no fallen.
     */
    private void ejecutarSentenciasMigracionV2V3() {
        List<String> sentencias = List.of(
                // ---- V2: mantmiento.responsable ----
                "ALTER TABLE mantenimiento ADD COLUMN IF NOT EXISTS responsable VARCHAR(150)",
                "UPDATE mantenimiento SET responsable = responsable_manual " +
                        "WHERE responsable IS NULL AND responsable_manual IS NOT NULL " +
                        "AND TRIM(responsable_manual) <> ''",
                "UPDATE mantenimiento SET responsable = 'Responsable asignado' WHERE responsable IS NULL",
                "ALTER TABLE mantenimiento ALTER COLUMN responsable SET NOT NULL",

                // ---- V2: limpiar columnas obsoletas ----
                // IMPORTANTE: "responsable_id" (FK a la tabla "responsable") NO se
                // elimina: conserva información histórica que no puede mapearse de
                // forma segura a la columna "responsable". Revisar manualmente en la
                // BD antes de decidir una migración definitiva.
                "ALTER TABLE mantenimiento DROP COLUMN IF EXISTS estado_anterior",
                "ALTER TABLE mantenimiento DROP COLUMN IF EXISTS responsable_manual",

                // ---- Otras columnas de mantenmiento que la entidad exige ----
                "ALTER TABLE mantenimiento ADD COLUMN IF NOT EXISTS fecha_mantenimiento TIMESTAMP",
                "UPDATE mantenimiento SET fecha_mantenimiento = COALESCE(fecha_hora, now()) " +
                        "WHERE fecha_mantenimiento IS NULL",
                "ALTER TABLE mantenimiento ALTER COLUMN fecha_mantenimiento SET NOT NULL",

                "ALTER TABLE mantenimiento ADD COLUMN IF NOT EXISTS estado_mantenimiento VARCHAR(100)",
                "UPDATE mantenimiento SET estado_mantenimiento = 'Finalizado' WHERE estado_mantenimiento IS NULL",
                "ALTER TABLE mantenimiento ALTER COLUMN estado_mantenimiento SET NOT NULL",

                "ALTER TABLE mantenimiento ADD COLUMN IF NOT EXISTS estado_posterior VARCHAR(50)",
                "UPDATE mantenimiento SET estado_posterior = 'OPERATIVO' WHERE estado_posterior IS NULL",
                "ALTER TABLE mantenimiento ALTER COLUMN estado_posterior SET NOT NULL",

                "ALTER TABLE mantenimiento ADD COLUMN IF NOT EXISTS observaciones TEXT",
                "UPDATE mantenimiento SET observaciones = 'Sin observaciones' " +
                        "WHERE observaciones IS NULL OR TRIM(observaciones) = ''",
                "ALTER TABLE mantenimiento ALTER COLUMN observaciones SET NOT NULL",

                // ---- V3: funcionarios.nombres y apellidos ----
                "ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS nombres VARCHAR(150)",
                "UPDATE funcionarios SET nombres = nombre_pila " +
                        "WHERE (nombres IS NULL OR TRIM(nombres) = '') " +
                        "AND nombre_pila IS NOT NULL AND TRIM(nombre_pila) <> ''",
                "UPDATE funcionarios SET nombres = '' WHERE nombres IS NULL",
                "ALTER TABLE funcionarios ALTER COLUMN nombres SET NOT NULL",

                "ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS apellidos VARCHAR(150)",
                "UPDATE funcionarios SET apellidos = '' WHERE apellidos IS NULL",
                "ALTER TABLE funcionarios ALTER COLUMN apellidos SET NOT NULL"
        );

        for (String sql : sentencias) {
            try {
                jdbcTemplate.execute(sql);
            } catch (Exception ex) {
                log.warn("Migración V2/V3: sentencia omitida ({}): {}", ex.getMessage(), sql);
            }
        }
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