package ec.gob.tic.sistema_tic.repository;

import ec.gob.tic.sistema_tic.dto.ResumenEquipamientoDTO;
import ec.gob.tic.sistema_tic.entity.EquipoTecnologico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EquipoTecnologicoRepository extends JpaRepository<EquipoTecnologico, Long> {

    @Query("SELECT e FROM EquipoTecnologico e LEFT JOIN FETCH e.funcionario ORDER BY e.id DESC")
    List<EquipoTecnologico> findAllWithFuncionario();

    @Query("SELECT e FROM EquipoTecnologico e LEFT JOIN FETCH e.funcionario WHERE e.id = :id")
    Optional<EquipoTecnologico> findByIdWithFuncionario(@Param("id") Long id);

    @Query("SELECT e FROM EquipoTecnologico e LEFT JOIN FETCH e.funcionario WHERE e.funcionario.id = :funcionarioId ORDER BY e.id DESC")
    List<EquipoTecnologico> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId);

    @Query("SELECT e FROM EquipoTecnologico e LEFT JOIN FETCH e.funcionario WHERE " +
            "(:cedula = '' OR (e.funcionario IS NOT NULL AND e.funcionario.cedula LIKE CONCAT('%', :cedula, '%'))) AND " +
            "(:serie = '' OR LOWER(e.serie) LIKE LOWER(CONCAT('%', :serie, '%'))) AND " +
            "(:tipo = '' OR LOWER(e.tipoEquipo) LIKE LOWER(CONCAT('%', :tipo, '%'))) AND " +
            "(:marca = '' OR LOWER(e.marca) LIKE LOWER(CONCAT('%', :marca, '%'))) AND " +
            "(:modelo = '' OR LOWER(e.modelo) LIKE LOWER(CONCAT('%', :modelo, '%'))) AND " +
            "(:etiqueta = '' OR LOWER(e.etiquetaConstatacion) LIKE LOWER(CONCAT('%', :etiqueta, '%'))) AND " +
            "(:nroPR = '' OR LOWER(e.nroPR) LIKE LOWER(CONCAT('%', :nroPR, '%'))) AND " +
            "(:ipTelefono = '' OR LOWER(e.ipTelefono) LIKE LOWER(CONCAT('%', :ipTelefono, '%'))) " +
            "ORDER BY e.id DESC")
    List<EquipoTecnologico> buscarConFiltros(
            @Param("cedula") String cedula,
            @Param("serie") String serie,
            @Param("tipo") String tipo,
            @Param("marca") String marca,
            @Param("modelo") String modelo,
            @Param("etiqueta") String etiqueta,
            @Param("nroPR") String nroPR,
            @Param("ipTelefono") String ipTelefono);

    @Query("SELECT e FROM EquipoTecnologico e LEFT JOIN FETCH e.funcionario WHERE " +
            "(:serie = '' OR LOWER(e.serie) LIKE LOWER(CONCAT('%', :serie, '%'))) AND " +
            "(:cedula = '' OR (e.funcionario IS NOT NULL AND e.funcionario.cedula LIKE CONCAT('%', :cedula, '%'))) " +
            "ORDER BY e.id DESC")
    List<EquipoTecnologico> buscarPorSerieYCedula(@Param("serie") String serie, @Param("cedula") String cedula);

    @Query("SELECT new ec.gob.tic.sistema_tic.dto.ResumenEquipamientoDTO(e.tipoEquipo, COUNT(e)) " +
            "FROM EquipoTecnologico e GROUP BY e.tipoEquipo ORDER BY e.tipoEquipo")
    List<ResumenEquipamientoDTO> resumenPorTipoEquipo();

    @Query("SELECT new ec.gob.tic.sistema_tic.dto.ResumenEquipamientoDTO(e.tipoEquipo, COUNT(e)) " +
            "FROM EquipoTecnologico e " +
            "WHERE e.fechaCreacion >= :desde AND e.fechaCreacion < :hasta " +
            "GROUP BY e.tipoEquipo ORDER BY e.tipoEquipo")
    List<ResumenEquipamientoDTO> resumenPorTipoEntreFechas(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);
}