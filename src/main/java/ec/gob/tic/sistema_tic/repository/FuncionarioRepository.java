package ec.gob.tic.sistema_tic.repository;

import ec.gob.tic.sistema_tic.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Optional<Funcionario> findByCedula(String cedula);

    Optional<Funcionario> findByCodigoBiometrico(String codigoBiometrico);

    @Query("SELECT f FROM Funcionario f WHERE LOWER(f.cedula) LIKE LOWER(CONCAT('%', :cedula, '%')) ORDER BY f.id DESC")
    List<Funcionario> findByCedulaContainingIgnoreCase(@Param("cedula") String cedula);

    @Query("SELECT f FROM Funcionario f WHERE " +
            "LOWER(f.cedula) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(COALESCE(f.nombres, '')) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(COALESCE(f.apellidos, '')) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(COALESCE(f.nombrePila, '')) LIKE LOWER(CONCAT('%', :termino, '%')) " +
            "ORDER BY f.id DESC")
    List<Funcionario> buscarPorTermino(@Param("termino") String termino);
}
