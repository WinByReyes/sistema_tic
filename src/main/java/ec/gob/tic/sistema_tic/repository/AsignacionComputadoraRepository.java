package ec.gob.tic.sistema_tic.repository;

import ec.gob.tic.sistema_tic.entity.AsignacionComputadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AsignacionComputadoraRepository
        extends JpaRepository<AsignacionComputadora, Long> {

    @Query("SELECT a FROM AsignacionComputadora a LEFT JOIN FETCH a.funcionario WHERE a.computadora.id = :computadoraId ORDER BY a.fechaAsignacion DESC")
    List<AsignacionComputadora>
    findByComputadoraIdOrderByFechaAsignacionDesc(@Param("computadoraId") Long computadoraId);

    @Query("SELECT a FROM AsignacionComputadora a LEFT JOIN FETCH a.computadora WHERE a.funcionario.id = :funcionarioId ORDER BY a.fechaAsignacion DESC")
    List<AsignacionComputadora>
    findByFuncionarioIdOrderByFechaAsignacionDesc(@Param("funcionarioId") Long funcionarioId);

    Optional<AsignacionComputadora>
    findByComputadoraIdAndFechaFinIsNull(Long computadoraId);
}
