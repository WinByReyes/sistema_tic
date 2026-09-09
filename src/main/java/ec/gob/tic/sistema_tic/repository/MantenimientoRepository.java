package ec.gob.tic.sistema_tic.repository;

import ec.gob.tic.sistema_tic.entity.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    @Query("SELECT m FROM Mantenimiento m " +
            "LEFT JOIN FETCH m.computadora c " +
            "LEFT JOIN FETCH c.funcionario f " +
            "LEFT JOIN FETCH m.usuario u " +
            "ORDER BY m.id DESC")
    List<Mantenimiento> findAllWithDetails();

    @Query("SELECT m FROM Mantenimiento m " +
            "LEFT JOIN FETCH m.computadora c " +
            "LEFT JOIN FETCH c.funcionario f " +
            "LEFT JOIN FETCH m.usuario u " +
            "WHERE m.id = :id")
    Optional<Mantenimiento> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT m FROM Mantenimiento m " +
            "LEFT JOIN FETCH m.computadora c " +
            "LEFT JOIN FETCH c.funcionario f " +
            "LEFT JOIN FETCH m.usuario u " +
            "WHERE (:serie IS NULL OR LOWER(c.serie) LIKE LOWER(CONCAT('%', :serie, '%'))) " +
            "AND (:cedula IS NULL OR (f IS NOT NULL AND LOWER(f.cedula) LIKE LOWER(CONCAT('%', :cedula, '%')))) " +
            "ORDER BY m.id DESC")
    List<Mantenimiento> buscarPorSerieYCedula(@Param("serie") String serie, @Param("cedula") String cedula);

    List<Mantenimiento> findByComputadoraId(Long computadoraId);

    List<Mantenimiento> findByUsuarioId(Long usuarioId);

    List<Mantenimiento> findByTipoMantenimiento(String tipoMantenimiento);

    List<Mantenimiento> findByComputadoraFuncionarioId(Long funcionarioId);

    @Query("SELECT m FROM Mantenimiento m " +
            "LEFT JOIN FETCH m.computadora c " +
            "LEFT JOIN FETCH c.funcionario f " +
            "LEFT JOIN FETCH m.usuario u " +
            "WHERE m.fechaMantenimiento BETWEEN :desde AND :hasta " +
            "ORDER BY m.fechaMantenimiento DESC")
    List<Mantenimiento> findByFechaMantenimientoBetweenOrderByFechaMantenimientoDesc(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}
