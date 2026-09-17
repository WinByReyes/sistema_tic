package ec.gob.tic.sistema_tic.service;


import ec.gob.tic.sistema_tic.dto.AsignacionComputadoraResponseDTO;
import ec.gob.tic.sistema_tic.dto.ComputadoraResponseDTO;
import ec.gob.tic.sistema_tic.dto.ConsultaComputadoraResponseDTO;
import ec.gob.tic.sistema_tic.dto.ConsultaFuncionarioResponseDTO;
import ec.gob.tic.sistema_tic.dto.FuncionarioResponseDTO;
import ec.gob.tic.sistema_tic.dto.MantenimientoResponseDTO;

import ec.gob.tic.sistema_tic.entity.Computadora;
import ec.gob.tic.sistema_tic.entity.Funcionario;
import ec.gob.tic.sistema_tic.entity.Mantenimiento;

import ec.gob.tic.sistema_tic.exception.RecursoNoEncontradoException;

import ec.gob.tic.sistema_tic.repository.AsignacionComputadoraRepository;
import ec.gob.tic.sistema_tic.repository.ComputadoraRepository;
import ec.gob.tic.sistema_tic.repository.FuncionarioRepository;
import ec.gob.tic.sistema_tic.repository.MantenimientoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ConsultaService {


    private final FuncionarioRepository
            funcionarioRepository;


    private final ComputadoraRepository
            computadoraRepository;


    private final MantenimientoRepository
            mantenimientoRepository;


    private final AsignacionComputadoraRepository
            asignacionRepository;


    public ConsultaService(

            FuncionarioRepository
                    funcionarioRepository,

            ComputadoraRepository
                    computadoraRepository,

            MantenimientoRepository
                    mantenimientoRepository,

            AsignacionComputadoraRepository
                    asignacionRepository) {


        this.funcionarioRepository =
                funcionarioRepository;


        this.computadoraRepository =
                computadoraRepository;


        this.mantenimientoRepository =
                mantenimientoRepository;


        this.asignacionRepository =
                asignacionRepository;

    }


    // ========================================
    // CONSULTA FUNCIONARIO
    // ========================================

    @Transactional(readOnly = true)
    public ConsultaFuncionarioResponseDTO
    consultarPorCedula(
            String cedula) {


        Funcionario funcionario =

                funcionarioRepository
                        .findByCedula(cedula)

                        .orElseThrow(

                                () ->
                                        new RecursoNoEncontradoException(

                                                "No existe un funcionario " +
                                                        "con la cédula: " +
                                                        cedula

                                        )

                        );


        List<Computadora> computadoras =

                computadoraRepository
                        .findByFuncionarioId(
                                funcionario.getId()
                        );


        List<Mantenimiento> mantenimientos =

                mantenimientoRepository
                        .findByComputadoraFuncionarioId(
                                funcionario.getId()
                        );


        return new ConsultaFuncionarioResponseDTO(

                convertirFuncionario(
                        funcionario
                ),


                computadoras

                        .stream()

                        .map(
                                ComputadoraResponseDTO::new
                        )

                        .toList(),


                mantenimientos

                        .stream()

                        .map(
                                this::convertirMantenimiento
                        )

                        .toList()

        );

    }


    // ========================================
    // CONSULTA COMPUTADORA
    // ========================================

    @Transactional(readOnly = true)
    public List<ConsultaComputadoraResponseDTO>
    consultarComputadoras(

            String nombreEquipo,

            String serie,

            String cedula) {


        List<Computadora> computadoras;


        boolean tieneNombre =

                nombreEquipo != null &&
                        !nombreEquipo.isBlank();


        boolean tieneSerie =

                serie != null &&
                        !serie.isBlank();


        boolean tieneCedula =

                cedula != null &&
                        !cedula.isBlank();


        if (!tieneNombre && !tieneSerie && !tieneCedula) {

            throw new IllegalArgumentException(

                    "Debe ingresar la cédula del " +
                            "funcionario, la serie del " +
                            "equipo o ambos."

            );

        }


        // ----------------------------------------
        // CÉDULA (sola o combinada con serie/nombre)
        // ----------------------------------------

        if (tieneCedula) {


            if (funcionarioRepository

                    .findByCedulaContaining(
                            cedula.trim()
                    )

                    .isEmpty()) {

                throw new RecursoNoEncontradoException(

                        "No existe un funcionario " +
                                "con la cédula: " +
                                cedula.trim()

                );

            }


            String serieFiltro =
                    tieneSerie
                            ? serie.trim()
                            : "";


            computadoras =

                    computadoraRepository

                            .buscarPorSerieYCedula(
                                    serieFiltro,
                                    cedula.trim()
                            );


            if (tieneNombre) {

                String nombreFiltro =
                        nombreEquipo.toLowerCase();

                computadoras =

                        computadoras

                                .stream()

                                .filter(
                                        computadora ->

                                                computadora
                                                        .getNombreEquipo() != null

                                                        &&

                                                        computadora
                                                                .getNombreEquipo()
                                                                .toLowerCase()
                                                                .contains(
                                                                        nombreFiltro
                                                                )
                                )

                                .toList();

            }


        }

        // ----------------------------------------
        // NOMBRE + SERIE
        // ----------------------------------------

        else if (
                tieneNombre &&
                        tieneSerie
        ) {


            computadoras =

                    computadoraRepository

                            .findByNombreEquipoContainingIgnoreCase(
                                    nombreEquipo
                            )

                            .stream()

                            .filter(
                                    computadora ->

                                            computadora
                                                    .getSerie() != null

                                                    &&

                                                    computadora
                                                            .getSerie()
                                                            .toLowerCase()
                                                            .contains(
                                                                    serie.toLowerCase()
                                                            )
                            )

                            .toList();


        }

        // ----------------------------------------
        // SOLO NOMBRE
        // ----------------------------------------

        else if (tieneNombre) {


            computadoras =

                    computadoraRepository

                            .findByNombreEquipoContainingIgnoreCase(
                                    nombreEquipo
                            );

        }

        // ----------------------------------------
        // SOLO SERIE
        // ----------------------------------------

        else {


            computadoras =

                    computadoraRepository

                            .findBySerieContainingIgnoreCase(
                                    serie
                            );

        }


        return computadoras

                .stream()

                .map(
                        this::construirConsultaComputadora
                )

                .toList();

    }


    // ========================================
    // CONSTRUIR CONSULTA COMPUTADORA
    // ========================================

    private ConsultaComputadoraResponseDTO
    construirConsultaComputadora(

            Computadora computadora) {


        /*
         * Funcionario que actualmente
         * tiene asignada la computadora.
         */

        Funcionario funcionarioActual =
                computadora.getFuncionario();


        // ----------------------------------------
        // HISTORIAL DE ASIGNACIONES
        // ----------------------------------------

        List<AsignacionComputadoraResponseDTO>
                historial =

                asignacionRepository

                        .findByComputadoraIdOrderByFechaAsignacionDesc(
                                computadora.getId()
                        )

                        .stream()

                        .map(
                                AsignacionComputadoraResponseDTO::new
                        )

                        .toList();


        // ----------------------------------------
        // HISTORIAL DE MANTENIMIENTOS
        // ----------------------------------------

        List<MantenimientoResponseDTO>
                mantenimientos =

                mantenimientoRepository

                        .findByComputadoraId(
                                computadora.getId()
                        )

                        .stream()

                        .sorted(

                                (a, b) -> {

                                    LocalDateTime fechaA =
                                            a.getFechaMantenimiento();


                                    LocalDateTime fechaB =
                                            b.getFechaMantenimiento();


                                    if (
                                            fechaA == null &&
                                                    fechaB == null
                                    ) {

                                        return 0;

                                    }


                                    if (fechaA == null) {

                                        return 1;

                                    }


                                    if (fechaB == null) {

                                        return -1;

                                    }


                                    return fechaB.compareTo(
                                            fechaA
                                    );

                                }

                        )

                        .map(
                                this::convertirMantenimiento
                        )

                        .toList();


        return new ConsultaComputadoraResponseDTO(

                new ComputadoraResponseDTO(
                        computadora
                ),


                funcionarioActual == null

                        ? null

                        : convertirFuncionario(
                        funcionarioActual
                ),


                historial,


                mantenimientos

        );

    }


    // ========================================
    // CONSULTAR MANTENIMIENTOS POR FECHA
    // ========================================

    @Transactional(readOnly = true)
    public List<MantenimientoResponseDTO>
    buscarMantenimientosPorFecha(

            LocalDateTime desde,

            LocalDateTime hasta) {


        return mantenimientoRepository

                .findByFechaMantenimientoBetweenOrderByFechaMantenimientoDesc(
                        desde,
                        hasta
                )

                .stream()

                .map(
                        this::convertirMantenimiento
                )

                .toList();

    }


    // ========================================
    // CONVERTIR FUNCIONARIO
    // ========================================

    private FuncionarioResponseDTO
    convertirFuncionario(
            Funcionario funcionario) {


        FuncionarioResponseDTO dto =
                new FuncionarioResponseDTO();


        dto.setId(
                funcionario.getId()
        );


        dto.setCedula(
                funcionario.getCedula()
        );


        dto.setNombrePila(
                funcionario.getNombrePila()
        );


        dto.setUnidadAdministrativa(
                funcionario
                        .getUnidadAdministrativa()
        );


        dto.setCargo(
                funcionario.getCargo()
        );


        dto.setEstado(
                funcionario.getEstado()
        );


        dto.setCodigoBiometrico(
                funcionario
                        .getCodigoBiometrico()
        );


        dto.setFechaCreacion(
                funcionario.getFechaCreacion()
        );


        return dto;

    }


    // ========================================
    // CONVERTIR MANTENIMIENTO
    // ========================================

    private MantenimientoResponseDTO
    convertirMantenimiento(

            Mantenimiento mantenimiento) {


        MantenimientoResponseDTO dto =
                new MantenimientoResponseDTO();


        dto.setId(
                mantenimiento.getId()
        );


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


        dto.setTipoEquipo(
                mantenimiento
                        .getComputadora()
                        .getTipo()
        );


        dto.setMarcaEquipo(
                mantenimiento
                        .getComputadora()
                        .getMarca()
        );


        dto.setModeloEquipo(
                mantenimiento
                        .getComputadora()
                        .getModelo()
        );


        dto.setUbicacionEquipo(
                mantenimiento
                        .getComputadora()
                        .getUbicacion()
        );


        dto.setEstadoActualComputadora(
                mantenimiento
                        .getComputadora()
                        .getEstado()
        );


        if (mantenimiento.getComputadora().getFuncionario() != null) {

            dto.setFuncionarioId(
                    mantenimiento
                            .getComputadora()
                            .getFuncionario()
                            .getId()
            );

            dto.setCedulaFuncionario(
                    mantenimiento
                            .getComputadora()
                            .getFuncionario()
                            .getCedula()
            );

            dto.setNombreFuncionario(
                    mantenimiento
                            .getComputadora()
                            .getFuncionario()
                            .getNombrePila()
            );

        }


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


        // Responsable (valor del catálogo RESPONSABLE_MANTENIMIENTO)
        dto.setNombreResponsable(mantenimiento.getResponsable());


        dto.setFechaHora(
                mantenimiento.getFechaHora()
        );

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


        dto.setEstadoPosterior(
                mantenimiento.getEstadoPosterior()
        );


        dto.setObservaciones(
                mantenimiento.getObservaciones()
        );


        dto.setFechaCreacion(
                mantenimiento.getFechaCreacion()
        );


        dto.setFechaMantenimiento(
                mantenimiento
                        .getFechaMantenimiento()
        );


        return dto;

    }

}
