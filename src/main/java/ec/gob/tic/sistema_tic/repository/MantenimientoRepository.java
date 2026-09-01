package ec.gob.tic.sistema_tic.repository;

import ec.gob.tic.sistema_tic.entity.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MantenimientoRepository
        extends JpaRepository<Mantenimiento, Long> {

    List<Mantenimiento> findByComputadoraId(Long computadoraId);

    List<Mantenimiento> findByUsuarioId(Long usuarioId);

    List<Mantenimiento> findByTipoMantenimiento(String tipoMantenimiento);

    List<Mantenimiento> findByComputadoraFuncionarioId(Long funcionarioId);

    List<Mantenimiento>
    findByFechaMantenimientoBetweenOrderByFechaMantenimientoDesc(
            LocalDateTime desde,
            LocalDateTime hasta
    );
}