package ec.gob.tic.sistema_tic.repository;

import ec.gob.tic.sistema_tic.entity.Impresora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImpresoraRepository extends JpaRepository<Impresora, Long> {

    @Query("SELECT i FROM Impresora i LEFT JOIN FETCH i.funcionario ORDER BY i.id DESC")
    List<Impresora> findAllWithFuncionario();

    @Query("SELECT i FROM Impresora i LEFT JOIN FETCH i.funcionario WHERE i.id = :id")
    Optional<Impresora> findByIdWithFuncionario(@Param("id") Long id);

    @Query("SELECT i FROM Impresora i LEFT JOIN FETCH i.funcionario WHERE i.funcionario.id = :funcionarioId ORDER BY i.id DESC")
    List<Impresora> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId);

    @Query("SELECT i FROM Impresora i LEFT JOIN FETCH i.funcionario WHERE " +
            "(:cedula = '' OR (i.funcionario IS NOT NULL AND i.funcionario.cedula LIKE CONCAT('%', :cedula, '%'))) AND " +
            "(:serie = '' OR LOWER(i.serie) LIKE LOWER(CONCAT('%', :serie, '%'))) AND " +
            "(:tipo = '' OR LOWER(i.tipoEquipo) LIKE LOWER(CONCAT('%', :tipo, '%'))) AND " +
            "(:marca = '' OR LOWER(i.marca) LIKE LOWER(CONCAT('%', :marca, '%'))) AND " +
            "(:modelo = '' OR LOWER(i.modelo) LIKE LOWER(CONCAT('%', :modelo, '%'))) " +
            "ORDER BY i.id DESC")
    List<Impresora> buscarConFiltros(
            @Param("cedula") String cedula,
            @Param("serie") String serie,
            @Param("tipo") String tipo,
            @Param("marca") String marca,
            @Param("modelo") String modelo);
}