package ec.gob.tic.sistema_tic.repository;

import ec.gob.tic.sistema_tic.entity.Computadora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComputadoraRepository extends JpaRepository<Computadora, Long> {
    Optional<Computadora> findBySerie(String serie);

    List<Computadora> findByFuncionarioId(Long funcionarioId);

    List<Computadora>
    findByNombreEquipoContainingIgnoreCase(
            String nombreEquipo
    );

    List<Computadora>
    findBySerieContainingIgnoreCase(
            String serie
    );
}
