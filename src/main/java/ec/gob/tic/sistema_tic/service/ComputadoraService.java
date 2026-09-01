package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.ComputadoraRequestDTO;
import ec.gob.tic.sistema_tic.entity.AsignacionComputadora;
import ec.gob.tic.sistema_tic.entity.Computadora;
import ec.gob.tic.sistema_tic.entity.Funcionario;
import ec.gob.tic.sistema_tic.exception.RecursoNoEncontradoException;
import ec.gob.tic.sistema_tic.repository.AsignacionComputadoraRepository;
import ec.gob.tic.sistema_tic.repository.ComputadoraRepository;
import ec.gob.tic.sistema_tic.repository.FuncionarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ComputadoraService {


    private final ComputadoraRepository computadoraRepository;

    private final FuncionarioRepository funcionarioRepository;

    private final AsignacionComputadoraRepository asignacionRepository;


    public ComputadoraService(

            ComputadoraRepository computadoraRepository,

            FuncionarioRepository funcionarioRepository,

            AsignacionComputadoraRepository asignacionRepository) {

        this.computadoraRepository =
                computadoraRepository;

        this.funcionarioRepository =
                funcionarioRepository;

        this.asignacionRepository =
                asignacionRepository;
    }


    // =========================================================
    // LISTAR
    // =========================================================

    @Transactional(readOnly = true)
    public List<Computadora> listarTodas() {

        return computadoraRepository.findAll();

    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    @Transactional(readOnly = true)
    public Computadora buscarPorId(Long id) {

        return computadoraRepository
                .findById(id)

                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Computadora no encontrada con id: "
                                        + id
                        )
                );

    }


    // =========================================================
    // BUSCAR POR SERIE
    // =========================================================

    @Transactional(readOnly = true)
    public Computadora buscarPorSerie(String serie) {

        return computadoraRepository
                .findBySerie(serie)

                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Computadora no encontrada con serie: "
                                        + serie
                        )
                );

    }


    // =========================================================
    // CREAR COMPUTADORA
    // =========================================================

    @Transactional
    public Computadora guardar(
            ComputadoraRequestDTO datos) {


        Computadora computadora =
                new Computadora();


        cargarDatosComputadora(
                computadora,
                datos
        );


        /*
         * Una computadora puede crearse
         * sin funcionario.
         */

        Funcionario funcionario =
                buscarFuncionarioOpcional(
                        datos.getCedulaFuncionario()
                );


        if (funcionario != null) {

            validarFuncionarioActivo(
                    funcionario
            );

        }


        computadora.setFuncionario(
                funcionario
        );


        Computadora guardada =
                computadoraRepository.save(
                        computadora
                );


        /*
         * Si la computadora nació asignada,
         * también creamos su historial.
         */

        if (funcionario != null) {

            crearAsignacion(

                    guardada,

                    funcionario,

                    "Asignación inicial de computadora"

            );

        }


        return guardada;

    }


    // =========================================================
    // ACTUALIZAR COMPUTADORA
    // =========================================================

    @Transactional
    public Computadora actualizar(

            Long id,

            ComputadoraRequestDTO datos) {


        Computadora computadora =
                computadoraRepository
                        .findById(id)

                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Computadora no encontrada con ID: "
                                                + id
                                )
                        );


        /*
         * Guardamos quién era el funcionario
         * antes de realizar cualquier cambio.
         */

        Funcionario funcionarioAnterior =
                computadora.getFuncionario();


        /*
         * Buscamos el nuevo funcionario.
         *
         * Puede ser NULL.
         */

        Funcionario nuevoFuncionario =
                buscarFuncionarioOpcional(
                        datos.getCedulaFuncionario()
                );


        if (nuevoFuncionario != null) {

            validarFuncionarioActivo(
                    nuevoFuncionario
            );

        }


        /*
         * Actualizamos los datos técnicos
         * de la computadora.
         */

        cargarDatosComputadora(
                computadora,
                datos
        );


        /*
         * Determinamos si realmente cambió
         * la asignación.
         */

        Long idAnterior =
                funcionarioAnterior != null
                        ? funcionarioAnterior.getId()
                        : null;


        Long idNuevo =
                nuevoFuncionario != null
                        ? nuevoFuncionario.getId()
                        : null;


        boolean cambioAsignacion =

                !java.util.Objects.equals(
                        idAnterior,
                        idNuevo
                );


        /*
         * Si cambió el funcionario:
         *
         * 1. Cerramos la asignación anterior.
         * 2. Cambiamos funcionario actual.
         * 3. Creamos nueva asignación
         *    si existe nuevo funcionario.
         */

        if (cambioAsignacion) {


            cerrarAsignacionActual(
                    computadora
            );


            computadora.setFuncionario(
                    nuevoFuncionario
            );


            if (nuevoFuncionario != null) {

                crearAsignacion(

                        computadora,

                        nuevoFuncionario,

                        "Nueva asignación mediante actualización"

                );

            }

        }


        return computadoraRepository.save(
                computadora
        );

    }


    // =========================================================
    // ASIGNAR FUNCIONARIO
    // =========================================================

    @Transactional
    public Computadora asignarFuncionario(

            Long computadoraId,

            String cedulaFuncionario) {


        Computadora computadora =
                computadoraRepository
                        .findById(computadoraId)

                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Computadora no encontrada"
                                )
                        );


        if (
                cedulaFuncionario == null ||
                        cedulaFuncionario.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Debe proporcionar la cédula del funcionario."
            );

        }


        Funcionario nuevoFuncionario =
                funcionarioRepository
                        .findByCedula(
                                cedulaFuncionario
                        )

                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Funcionario no encontrado"
                                )
                        );


        validarFuncionarioActivo(
                nuevoFuncionario
        );


        /*
         * Si ya pertenece al mismo funcionario,
         * no creamos otra fila en el historial.
         */

        if (
                computadora.getFuncionario() != null &&

                        computadora
                                .getFuncionario()
                                .getId()
                                .equals(
                                        nuevoFuncionario.getId()
                                )
        ) {

            return computadora;

        }


        /*
         * Cerramos cualquier asignación
         * que esté actualmente abierta.
         */

        cerrarAsignacionActual(
                computadora
        );


        /*
         * Actualizamos funcionario actual.
         */

        computadora.setFuncionario(
                nuevoFuncionario
        );


        Computadora guardada =
                computadoraRepository.save(
                        computadora
                );


        /*
         * Creamos la nueva asignación.
         */

        crearAsignacion(

                guardada,

                nuevoFuncionario,

                "Nueva asignación de computadora"

        );


        return guardada;

    }


    // =========================================================
    // DESASIGNAR FUNCIONARIO
    // =========================================================

    @Transactional
    public Computadora desasignarFuncionario(

            Long computadoraId) {


        Computadora computadora =
                computadoraRepository
                        .findById(computadoraId)

                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Computadora no encontrada"
                                )
                        );


        /*
         * Si existe asignación actual,
         * la cerramos.
         */

        cerrarAsignacionActual(
                computadora
        );


        /*
         * La computadora queda
         * sin funcionario actual.
         */

        computadora.setFuncionario(
                null
        );


        return computadoraRepository.save(
                computadora
        );

    }


    // =========================================================
    // ELIMINAR
    // =========================================================

    @Transactional
    public void eliminar(Long id) {


        Computadora computadora =
                computadoraRepository
                        .findById(id)

                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Computadora no encontrada con ID: "
                                                + id
                                )
                        );


        /*
         * No permitimos eliminar una computadora
         * que tenga historial.
         *
         * Esto protege la trazabilidad institucional.
         */

        if (
                asignacionRepository
                        .findByComputadoraIdOrderByFechaAsignacionDesc(
                                id
                        )
                        .size() > 0
        ) {

            throw new IllegalArgumentException(
                    "No se puede eliminar la computadora porque "
                            + "tiene historial de asignaciones. "
                            + "La computadora debe conservarse "
                            + "para mantener la trazabilidad."
            );

        }


        computadoraRepository.delete(
                computadora
        );

    }


    // =========================================================
    // CERRAR ASIGNACIÓN ACTUAL
    // =========================================================

    private void cerrarAsignacionActual(

            Computadora computadora) {


        asignacionRepository

                .findByComputadoraIdAndFechaFinIsNull(
                        computadora.getId()
                )

                .ifPresent(asignacion -> {

                    asignacion.setFechaFin(
                            LocalDateTime.now()
                    );


                    asignacionRepository.save(
                            asignacion
                    );

                });

    }


    // =========================================================
    // CREAR ASIGNACIÓN
    // =========================================================

    private void crearAsignacion(

            Computadora computadora,

            Funcionario funcionario,

            String observaciones) {


        AsignacionComputadora asignacion =
                new AsignacionComputadora();


        asignacion.setComputadora(
                computadora
        );


        asignacion.setFuncionario(
                funcionario
        );


        asignacion.setFechaAsignacion(
                LocalDateTime.now()
        );


        asignacion.setFechaFin(
                null
        );


        asignacion.setObservaciones(
                observaciones
        );


        asignacionRepository.save(
                asignacion
        );

    }


    // =========================================================
    // BUSCAR FUNCIONARIO OPCIONAL
    // =========================================================

    private Funcionario buscarFuncionarioOpcional(

            String cedula) {


        if (
                cedula == null ||
                        cedula.isBlank()
        ) {

            return null;

        }


        return funcionarioRepository
                .findByCedula(
                        cedula.trim()
                )

                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe un funcionario con la cédula: "
                                        + cedula
                        )
                );

    }


    // =========================================================
    // VALIDAR FUNCIONARIO ACTIVO
    // =========================================================

    private void validarFuncionarioActivo(

            Funcionario funcionario) {


        if (
                !"ACTIVO".equalsIgnoreCase(
                        funcionario.getEstado()
                )
        ) {

            throw new IllegalArgumentException(
                    "Solo se pueden asignar computadoras "
                            + "a funcionarios ACTIVO."
            );

        }

    }


    // =========================================================
    // CARGAR DATOS TÉCNICOS
    // =========================================================

    private void cargarDatosComputadora(

            Computadora computadora,

            ComputadoraRequestDTO datos) {


        computadora.setSerie(
                datos.getSerie()
        );


        computadora.setNombreEquipo(
                datos.getNombreEquipo()
        );


        computadora.setProcedencia(
                datos.getProcedencia()
        );


        computadora.setTipo(
                datos.getTipo()
        );


        computadora.setMarca(
                datos.getMarca()
        );


        computadora.setModelo(
                datos.getModelo()
        );


        computadora.setTipoProcesador(
                datos.getTipoProcesador()
        );


        computadora.setGeneracionProcesador(
                datos.getGeneracionProcesador()
        );


        computadora.setVelocidadProcesador(
                datos.getVelocidadProcesador()
        );


        computadora.setMemoriaRAM(
                datos.getMemoriaRAM()
        );


        computadora.setTipoDisco(
                datos.getTipoDisco()
        );


        computadora.setCapacidadDiscoGB(
                datos.getCapacidadDiscoGB()
        );


        computadora.setSistemaOperativo(
                datos.getSistemaOperativo()
        );


        computadora.setOffice(
                datos.getOffice()
        );


        computadora.setUbicacion(
                datos.getUbicacion()
        );

        computadora.setEstado(
                datos.getEstado()
        );

    }

}
