package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.MantenimientoRequestDTO;
import ec.gob.tic.sistema_tic.dto.MantenimientoResponseDTO;
import ec.gob.tic.sistema_tic.entity.Catalogo;
import ec.gob.tic.sistema_tic.entity.Computadora;
import ec.gob.tic.sistema_tic.entity.Mantenimiento;
import ec.gob.tic.sistema_tic.entity.Responsable;
import ec.gob.tic.sistema_tic.entity.Usuario;
import ec.gob.tic.sistema_tic.exception.RecursoNoEncontradoException;
import ec.gob.tic.sistema_tic.repository.CatalogoRepository;
import ec.gob.tic.sistema_tic.repository.ComputadoraRepository;
import ec.gob.tic.sistema_tic.repository.MantenimientoRepository;
import ec.gob.tic.sistema_tic.repository.ResponsableRepository;
import ec.gob.tic.sistema_tic.repository.UsuarioRepository;
import ec.gob.tic.sistema_tic.util.TextoUtil;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;

    private final ComputadoraRepository computadoraRepository;

    private final ResponsableRepository responsableRepository;

    private final UsuarioRepository usuarioRepository;

    private final CatalogoRepository catalogoRepository;


    public MantenimientoService(
            MantenimientoRepository mantenimientoRepository,
            ComputadoraRepository computadoraRepository,
            ResponsableRepository responsableRepository,
            UsuarioRepository usuarioRepository,
            CatalogoRepository catalogoRepository) {

        this.mantenimientoRepository =
                mantenimientoRepository;

        this.computadoraRepository =
                computadoraRepository;

        this.responsableRepository =
                responsableRepository;

        this.usuarioRepository =
                usuarioRepository;

        this.catalogoRepository =
                catalogoRepository;
    }


    // ==========================================
    // OBTENER USUARIO AUTENTICADO
    // ==========================================

    private Usuario obtenerUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (
                authentication == null ||
                        !authentication.isAuthenticated()
        ) {

            throw new IllegalArgumentException(
                    "No existe un usuario autenticado"
            );
        }


        String username =
                authentication.getName();


        return usuarioRepository
                .findByUsuario(username)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Usuario autenticado no encontrado"
                        )
                );
    }


    // ==========================================
    // VALIDAR CATÁLOGO
    // ==========================================

    private String validarCatalogoActivo(
            String tipo,
            String nombre) {

        if (
                nombre == null ||
                        nombre.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "El valor del catálogo es obligatorio"
            );
        }


        String valor =
                nombre.trim();


        Catalogo catalogo =
                catalogoRepository
                        .findByTipoAndNombreIgnoreCase(
                                tipo,
                                valor
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "El valor '"
                                                + valor
                                                + "' no existe en el catálogo "
                                                + tipo
                                )
                        );


        if (!Boolean.TRUE.equals(
                catalogo.getActivo()
        )) {

            throw new IllegalArgumentException(
                    "El valor '"
                            + valor
                            + "' está inactivo en el catálogo "
                            + tipo
            );
        }


        return catalogo.getNombre();
    }

    private void aplicarResponsable(
            Mantenimiento mantenimiento,
            MantenimientoRequestDTO datos) {

        boolean tieneResponsableId =
                datos.getResponsableId() != null;

        boolean tieneResponsableManual =
                datos.getResponsableManual() != null &&
                        !datos.getResponsableManual().trim().isEmpty();

        if (!tieneResponsableId && !tieneResponsableManual) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un responsable del catálogo "
                            + "o ingresar su nombre manualmente"
            );
        }

        // OPCIÓN POR DEFECTO: CATÁLOGO
        // Si llega un responsableId, tiene prioridad
        // y se ignora cualquier texto manual que
        // haya llegado junto con él.
        if (tieneResponsableId) {

            Responsable responsable =
                    responsableRepository
                            .findById(datos.getResponsableId())
                            .orElseThrow(() ->
                                    new RecursoNoEncontradoException(
                                            "El responsable no existe"
                                    )
                            );

            if (!Boolean.TRUE.equals(responsable.getEstado())) {
                throw new IllegalArgumentException(
                        "El responsable está inactivo"
                );
            }

            mantenimiento.setResponsable(responsable);
            mantenimiento.setResponsableManual(null);
            return;
        }

        // OPCIÓN NUEVA: TEXTO LIBRE
        mantenimiento.setResponsable(null);
        mantenimiento.setResponsableManual(
                TextoUtil.formatoNombre(datos.getResponsableManual())
        );
    }

    // ==========================================
    // CONVERTIR A RESPONSE
    // ==========================================

    private MantenimientoResponseDTO
    convertirAResponse(
            Mantenimiento mantenimiento) {

        MantenimientoResponseDTO dto =
                new MantenimientoResponseDTO();


        // ======================================
        // ID
        // ======================================

        dto.setId(
                mantenimiento.getId()
        );


        // ======================================
        // COMPUTADORA
        // ======================================

        dto.setComputadoraId(
                mantenimiento
                        .getComputadora()
                        .getId()
        );

        dto.setSerieComputadora(
                mantenimiento
                        .getComputadora()
                        .getSerie()
        );

        dto.setNombreEquipo(
                mantenimiento
                        .getComputadora()
                        .getNombreEquipo()
        );


        // ======================================
        // USUARIO
        // ======================================

        dto.setUsuarioId(
                mantenimiento
                        .getUsuario()
                        .getId()
        );

        dto.setNombreUsuario(
                mantenimiento
                        .getUsuario()
                        .getNombre()
        );

        dto.setUsuario(
                mantenimiento
                        .getUsuario()
                        .getUsuario()
        );


        // ======================================
        // RESPONSABLE
        // ======================================

        if (mantenimiento.getResponsable() != null) {

            dto.setResponsableId(mantenimiento.getResponsable().getId());
            dto.setNombreResponsable(mantenimiento.getResponsable().getNombre());
            dto.setCargoResponsable(mantenimiento.getResponsable().getCargo());

        } else {

            dto.setResponsableId(null);
            dto.setNombreResponsable(mantenimiento.getResponsableManual());
            dto.setCargoResponsable(null);
        }


        // ======================================
        // FECHAS
        // ======================================

        dto.setFechaHora(
                mantenimiento.getFechaHora()
        );

        dto.setFechaCreacion(
                mantenimiento.getFechaCreacion()
        );

        dto.setFechaMantenimiento(
                mantenimiento.getFechaMantenimiento()
        );


        // ======================================
        // MANTENIMIENTO
        // ======================================

        dto.setTipoMantenimiento(
                mantenimiento
                        .getTipoMantenimiento()
        );

        dto.setEstadoMantenimiento(
                mantenimiento
                        .getEstadoMantenimiento()
        );

        dto.setDiagnostico(
                mantenimiento.getDiagnostico()
        );

        dto.setTrabajoRealizado(
                mantenimiento.getTrabajoRealizado()
        );

        dto.setCosto(
                mantenimiento.getCosto()
        );


        // ======================================
        // ESTADO COMPUTADORA
        // ======================================

        dto.setEstadoAnterior(
                mantenimiento.getEstadoAnterior()
        );

        dto.setEstadoPosterior(
                mantenimiento.getEstadoPosterior()
        );


        // ======================================
        // OBSERVACIONES
        // ======================================

        dto.setObservaciones(
                mantenimiento.getObservaciones()
        );


        return dto;
    }


    // ==========================================
    // LISTAR
    // ==========================================

    @Transactional(readOnly = true)
    public List<MantenimientoResponseDTO>
    listarTodos() {

        return mantenimientoRepository
                .findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }


    // ==========================================
    // BUSCAR
    // ==========================================

    @Transactional(readOnly = true)
    public MantenimientoResponseDTO
    buscarPorId(Long id) {

        Mantenimiento mantenimiento =
                mantenimientoRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Mantenimiento no encontrado con ID: "
                                                + id
                                )
                        );


        return convertirAResponse(
                mantenimiento
        );
    }


    // ==========================================
    // CREAR
    // ==========================================

    @Transactional
    public MantenimientoResponseDTO
    guardar(
            MantenimientoRequestDTO datos) {

        Computadora computadora =
                computadoraRepository
                        .findById(
                                datos.getComputadoraId()
                        )
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "La computadora no existe"
                                )
                        );


        Usuario usuarioAutenticado =
                obtenerUsuarioAutenticado();


        // ======================================
        // VALIDAR CATÁLOGOS
        // ======================================

        String tipoMantenimiento =
                validarCatalogoActivo(
                        "TIPO_MANTENIMIENTO",
                        datos.getTipoMantenimiento()
                );


        String estadoMantenimiento =
                validarCatalogoActivo(
                        "ESTADO_MANTENIMIENTO",
                        datos.getEstadoMantenimiento()
                );


        Mantenimiento mantenimiento =
                new Mantenimiento();


        mantenimiento.setComputadora(
                computadora
        );

        mantenimiento.setUsuario(
                usuarioAutenticado
        );

        aplicarResponsable(
                mantenimiento,
                datos
        );

        mantenimiento.setTipoMantenimiento(
                tipoMantenimiento
        );

        mantenimiento.setEstadoMantenimiento(
                estadoMantenimiento
        );

        mantenimiento.setDiagnostico(
                datos.getDiagnostico()
        );

        mantenimiento.setTrabajoRealizado(
                datos.getTrabajoRealizado()
        );

        mantenimiento.setCosto(
                datos.getCosto()
        );

        mantenimiento.setEstadoAnterior(
                datos.getEstadoAnterior()
        );

        mantenimiento.setEstadoPosterior(
                datos.getEstadoPosterior()
        );

        mantenimiento.setObservaciones(
                datos.getObservaciones()
        );

        mantenimiento.setFechaMantenimiento(
                datos.getFechaMantenimiento()
        );


        // Sincronizar estado de la computadora si se especifica estadoPosterior
        if (datos.getEstadoPosterior() != null && !datos.getEstadoPosterior().isBlank()) {
            computadora.setEstado(datos.getEstadoPosterior());
            computadoraRepository.save(computadora);
        }

        Mantenimiento guardado =
                mantenimientoRepository
                        .save(mantenimiento);


        return convertirAResponse(
                guardado
        );
    }


    // ==========================================
    // ACTUALIZAR
    // ==========================================

    @Transactional
    public MantenimientoResponseDTO
    actualizar(
            Long id,
            MantenimientoRequestDTO datos) {

        Mantenimiento mantenimiento =
                mantenimientoRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Mantenimiento no encontrado"
                                )
                        );


        Computadora computadora =
                computadoraRepository
                        .findById(
                                datos.getComputadoraId()
                        )
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "La computadora no existe"
                                )
                        );


        // ======================================
        // VALIDAR CATÁLOGOS
        // ======================================

        String tipoMantenimiento =
                validarCatalogoActivo(
                        "TIPO_MANTENIMIENTO",
                        datos.getTipoMantenimiento()
                );


        String estadoMantenimiento =
                validarCatalogoActivo(
                        "ESTADO_MANTENIMIENTO",
                        datos.getEstadoMantenimiento()
                );


        /*
         * IMPORTANTE:
         *
         * El usuario que originalmente registró
         * el mantenimiento NO se modifica.
         */

        mantenimiento.setComputadora(
                computadora
        );

        aplicarResponsable(
                mantenimiento,
                datos
        );

        mantenimiento.setTipoMantenimiento(
                tipoMantenimiento
        );

        mantenimiento.setEstadoMantenimiento(
                estadoMantenimiento
        );

        mantenimiento.setDiagnostico(
                datos.getDiagnostico()
        );

        mantenimiento.setTrabajoRealizado(
                datos.getTrabajoRealizado()
        );

        mantenimiento.setCosto(
                datos.getCosto()
        );

        mantenimiento.setEstadoAnterior(
                datos.getEstadoAnterior()
        );

        mantenimiento.setEstadoPosterior(
                datos.getEstadoPosterior()
        );

        mantenimiento.setObservaciones(
                datos.getObservaciones()
        );

        mantenimiento.setFechaMantenimiento(
                datos.getFechaMantenimiento()
        );

        // Sincronizar estado de la computadora si se especifica estadoPosterior
        if (datos.getEstadoPosterior() != null && !datos.getEstadoPosterior().isBlank()) {
            computadora.setEstado(datos.getEstadoPosterior());
            computadoraRepository.save(computadora);
        }

        Mantenimiento actualizado =
                mantenimientoRepository
                        .save(mantenimiento);


        return convertirAResponse(
                actualizado
        );
    }


    // ==========================================
    // ELIMINAR
    // ==========================================

    @Transactional
    public void eliminar(Long id) {

        Mantenimiento mantenimiento =
                mantenimientoRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Mantenimiento no encontrado"
                                )
                        );


        mantenimientoRepository.delete(
                mantenimiento
        );
    }
}
