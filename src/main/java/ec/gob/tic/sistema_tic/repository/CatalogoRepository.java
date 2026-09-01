package ec.gob.tic.sistema_tic.repository;

import ec.gob.tic.sistema_tic.entity.Catalogo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CatalogoRepository
        extends JpaRepository<Catalogo, Long> {


    List<Catalogo>
    findByTipoAndActivoTrueOrderByNombreAsc(
            String tipo
    );


    List<Catalogo>
    findByTipoOrderByNombreAsc(
            String tipo
    );


    Optional<Catalogo>
    findByTipoAndNombreIgnoreCase(
            String tipo,
            String nombre
    );
}