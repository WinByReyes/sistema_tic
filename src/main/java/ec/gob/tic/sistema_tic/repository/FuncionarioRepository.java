package ec.gob.tic.sistema_tic.repository;

import ec.gob.tic.sistema_tic.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long>{
    Optional<Funcionario> findByCedula(String cedula);
}
