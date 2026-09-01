package ec.gob.tic.sistema_tic.repository;

import ec.gob.tic.sistema_tic.entity.AsignacionComputadora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsignacionComputadoraRepository
        extends JpaRepository<AsignacionComputadora, Long> {

    List<AsignacionComputadora>
    findByComputadoraIdOrderByFechaAsignacionDesc(Long computadoraId);

    List<AsignacionComputadora>
    findByFuncionarioIdOrderByFechaAsignacionDesc(Long funcionarioId);

    Optional<AsignacionComputadora>
    findByComputadoraIdAndFechaFinIsNull(Long computadoraId);

}