package ec.gob.tic.sistema_tic.repository;

import ec.gob.tic.sistema_tic.entity.Computadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ComputadoraRepository extends JpaRepository<Computadora, Long> {

    @Query("SELECT c FROM Computadora c LEFT JOIN FETCH c.funcionario ORDER BY c.id DESC")
    List<Computadora> findAllWithFuncionario();

    @Query("SELECT c FROM Computadora c LEFT JOIN FETCH c.funcionario WHERE c.id = :id")
    Optional<Computadora> findByIdWithFuncionario(@Param("id") Long id);

    @Query("SELECT c FROM Computadora c LEFT JOIN FETCH c.funcionario WHERE c.serie = :serie")
    Optional<Computadora> findBySerieWithFuncionario(@Param("serie") String serie);

    Optional<Computadora> findBySerie(String serie);

    @Query("SELECT c FROM Computadora c LEFT JOIN FETCH c.funcionario WHERE c.funcionario.id = :funcionarioId ORDER BY c.id DESC")
    List<Computadora> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId);

    @Query("SELECT c FROM Computadora c LEFT JOIN FETCH c.funcionario WHERE LOWER(c.nombreEquipo) LIKE LOWER(CONCAT('%', :nombreEquipo, '%')) ORDER BY c.id DESC")
    List<Computadora> findByNombreEquipoContainingIgnoreCase(@Param("nombreEquipo") String nombreEquipo);

    @Query("SELECT c FROM Computadora c LEFT JOIN FETCH c.funcionario WHERE LOWER(c.serie) LIKE LOWER(CONCAT('%', :serie, '%')) ORDER BY c.id DESC")
    List<Computadora> findBySerieContainingIgnoreCase(@Param("serie") String serie);

    @Query("SELECT c FROM Computadora c LEFT JOIN FETCH c.funcionario WHERE " +
            "(:serie IS NULL OR LOWER(c.serie) LIKE LOWER(CONCAT('%', cast(:serie as string), '%'))) AND " +
            "(:cedula IS NULL OR (c.funcionario IS NOT NULL AND LOWER(c.funcionario.cedula) LIKE LOWER(CONCAT('%', cast(:cedula as string), '%')))) " +
            "ORDER BY c.id DESC")
    List<Computadora> buscarPorSerieYCedula(@Param("serie") String serie, @Param("cedula") String cedula);
}
