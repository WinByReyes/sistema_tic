package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.ResponsableRequestDTO;
import ec.gob.tic.sistema_tic.dto.ResponsableResponseDTO;
import ec.gob.tic.sistema_tic.entity.Responsable;
import ec.gob.tic.sistema_tic.exception.RecursoNoEncontradoException;
import ec.gob.tic.sistema_tic.repository.ResponsableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResponsableService {

    private final ResponsableRepository responsableRepository;

    public ResponsableService(
            ResponsableRepository responsableRepository) {

        this.responsableRepository = responsableRepository;
    }

    @Transactional(readOnly = true)
    public List<ResponsableResponseDTO> listarTodos() {

        return responsableRepository
                .findAll()
                .stream()
                .sorted((a, b) ->
                        a.getNombre()
                                .compareToIgnoreCase(b.getNombre()))
                .map(ResponsableResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResponsableResponseDTO> listarActivos() {

        return responsableRepository
                .findByEstadoTrueOrderByNombreAsc()
                .stream()
                .map(ResponsableResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResponsableResponseDTO buscarPorId(Long id) {

        Responsable responsable =
                responsableRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Responsable no encontrado con ID: " + id
                                ));
        return new ResponsableResponseDTO(responsable);
    }

    @Transactional
    public ResponsableResponseDTO guardar(ResponsableRequestDTO datos) {

        Responsable responsable = new Responsable();
        responsable.setNombre(datos.getNombre());
        responsable.setCargo(datos.getCargo());
        responsable.setEstado(datos.getEstado() == null ? true : datos.getEstado());
        responsable.setFechaCreacion(LocalDateTime.now());

        Responsable guardado = responsableRepository.save(responsable);
        return new ResponsableResponseDTO(guardado);
    }

    @Transactional
    public ResponsableResponseDTO actualizar(
            Long id,
            ResponsableRequestDTO datos) {

        Responsable responsable =
                responsableRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Responsable no encontrado con ID: " + id
                                ));

        responsable.setNombre(
                datos.getNombre()
        );

        responsable.setCargo(
                datos.getCargo()
        );

        if (datos.getEstado() != null) {
            responsable.setEstado(
                    datos.getEstado()
            );
        }

        Responsable actualizado = responsableRepository.save(responsable);
        return new ResponsableResponseDTO(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {

        Responsable responsable =
                responsableRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Responsable no encontrado con ID: " + id
                                ));

        /*
         * No eliminamos físicamente.
         * Lo desactivamos para conservar
         * el historial.
         */

        responsable.setEstado(false);

        responsableRepository.save(responsable);
    }
}
