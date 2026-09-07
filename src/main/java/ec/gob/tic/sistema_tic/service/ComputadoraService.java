package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.ComputadoraRequestDTO;
import ec.gob.tic.sistema_tic.dto.ComputadoraResponseDTO;
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
        this.computadoraRepository = computadoraRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.asignacionRepository = asignacionRepository;
    }

    // =========================================================
    // LISTAR
    // =========================================================

    @Transactional(readOnly = true)
    public List<ComputadoraResponseDTO> listarTodas() {
        return computadoraRepository.findAllWithFuncionario()
                .stream()
                .map(ComputadoraResponseDTO::new)
                .toList();
    }

    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    @Transactional(readOnly = true)
    public ComputadoraResponseDTO buscarPorId(Long id) {
        Computadora computadora = computadoraRepository
                .findByIdWithFuncionario(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Computadora no encontrada con id: " + id
                        )
                );
        return new ComputadoraResponseDTO(computadora);
    }

    // =========================================================
    // BUSCAR POR SERIE
    // =========================================================

    @Transactional(readOnly = true)
    public ComputadoraResponseDTO buscarPorSerie(String serie) {
        Computadora computadora = computadoraRepository
                .findBySerieWithFuncionario(serie)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Computadora no encontrada con serie: " + serie
                        )
                );
        return new ComputadoraResponseDTO(computadora);
    }

    // =========================================================
    // CREAR COMPUTADORA
    // =========================================================

    @Transactional
    public ComputadoraResponseDTO guardar(ComputadoraRequestDTO datos) {
        Computadora computadora = new Computadora();
        cargarDatosComputadora(computadora, datos);

        Funcionario funcionario = buscarFuncionarioOpcional(datos.getCedulaFuncionario());
        if (funcionario != null) {
            validarFuncionarioActivo(funcionario);
        }

        computadora.setFuncionario(funcionario);
        Computadora guardada = computadoraRepository.save(computadora);

        if (funcionario != null) {
            crearAsignacion(guardada, funcionario, "Asignación inicial de computadora");
        }

        return new ComputadoraResponseDTO(guardada);
    }

    // =========================================================
    // ACTUALIZAR COMPUTADORA
    // =========================================================

    @Transactional
    public ComputadoraResponseDTO actualizar(Long id, ComputadoraRequestDTO datos) {
        Computadora computadora = computadoraRepository
                .findByIdWithFuncionario(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Computadora no encontrada con ID: " + id
                        )
                );

        Funcionario funcionarioAnterior = computadora.getFuncionario();
        Funcionario nuevoFuncionario = buscarFuncionarioOpcional(datos.getCedulaFuncionario());

        if (nuevoFuncionario != null) {
            validarFuncionarioActivo(nuevoFuncionario);
        }

        cargarDatosComputadora(computadora, datos);

        Long idAnterior = funcionarioAnterior != null ? funcionarioAnterior.getId() : null;
        Long idNuevo = nuevoFuncionario != null ? nuevoFuncionario.getId() : null;
        boolean cambioAsignacion = !java.util.Objects.equals(idAnterior, idNuevo);

        if (cambioAsignacion) {
            cerrarAsignacionActual(computadora);
            computadora.setFuncionario(nuevoFuncionario);

            if (nuevoFuncionario != null) {
                crearAsignacion(computadora, nuevoFuncionario, "Nueva asignación mediante actualización");
            }
        }

        Computadora actualizada = computadoraRepository.save(computadora);
        return new ComputadoraResponseDTO(actualizada);
    }

    // =========================================================
    // ASIGNAR FUNCIONARIO
    // =========================================================

    @Transactional
    public ComputadoraResponseDTO asignarFuncionario(Long computadoraId, String cedulaFuncionario) {
        Computadora computadora = computadoraRepository
                .findByIdWithFuncionario(computadoraId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Computadora no encontrada")
                );

        if (cedulaFuncionario == null || cedulaFuncionario.isBlank()) {
            throw new IllegalArgumentException("Debe proporcionar la cédula del funcionario.");
        }

        Funcionario nuevoFuncionario = funcionarioRepository
                .findByCedula(cedulaFuncionario)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Funcionario no encontrado")
                );

        validarFuncionarioActivo(nuevoFuncionario);

        if (computadora.getFuncionario() != null &&
                computadora.getFuncionario().getId().equals(nuevoFuncionario.getId())) {
            return new ComputadoraResponseDTO(computadora);
        }

        cerrarAsignacionActual(computadora);
        computadora.setFuncionario(nuevoFuncionario);
        Computadora guardada = computadoraRepository.save(computadora);
        crearAsignacion(guardada, nuevoFuncionario, "Nueva asignación de computadora");

        return new ComputadoraResponseDTO(guardada);
    }

    // =========================================================
    // DESASIGNAR FUNCIONARIO
    // =========================================================

    @Transactional
    public ComputadoraResponseDTO desasignarFuncionario(Long computadoraId) {
        Computadora computadora = computadoraRepository
                .findByIdWithFuncionario(computadoraId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Computadora no encontrada")
                );

        cerrarAsignacionActual(computadora);
        computadora.setFuncionario(null);
        Computadora guardada = computadoraRepository.save(computadora);

        return new ComputadoraResponseDTO(guardada);
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    @Transactional
    public void eliminar(Long id) {
        Computadora computadora = computadoraRepository
                .findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Computadora no encontrada con ID: " + id
                        )
                );

        if (asignacionRepository.findByComputadoraIdOrderByFechaAsignacionDesc(id).size() > 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar la computadora porque "
                            + "tiene historial de asignaciones. "
                            + "La computadora debe conservarse "
                            + "para mantener la trazabilidad."
            );
        }

        computadoraRepository.delete(computadora);
    }

    // =========================================================
    // CERRAR ASIGNACIÓN ACTUAL
    // =========================================================

    private void cerrarAsignacionActual(Computadora computadora) {
        asignacionRepository
                .findByComputadoraIdAndFechaFinIsNull(computadora.getId())
                .ifPresent(asignacion -> {
                    asignacion.setFechaFin(LocalDateTime.now());
                    asignacionRepository.save(asignacion);
                });
    }

    // =========================================================
    // CREAR ASIGNACIÓN
    // =========================================================

    private void crearAsignacion(Computadora computadora, Funcionario funcionario, String observaciones) {
        AsignacionComputadora asignacion = new AsignacionComputadora();
        asignacion.setComputadora(computadora);
        asignacion.setFuncionario(funcionario);
        asignacion.setFechaAsignacion(LocalDateTime.now());
        asignacion.setFechaFin(null);
        asignacion.setObservaciones(observaciones);
        asignacionRepository.save(asignacion);
    }

    // =========================================================
    // BUSCAR FUNCIONARIO OPCIONAL
    // =========================================================

    private Funcionario buscarFuncionarioOpcional(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            return null;
        }

        return funcionarioRepository
                .findByCedula(cedula.trim())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe un funcionario con la cédula: " + cedula
                        )
                );
    }

    // =========================================================
    // VALIDAR FUNCIONARIO ACTIVO
    // =========================================================

    private void validarFuncionarioActivo(Funcionario funcionario) {
        if (!"ACTIVO".equalsIgnoreCase(funcionario.getEstado())) {
            throw new IllegalArgumentException(
                    "Solo se pueden asignar computadoras a funcionarios ACTIVO."
            );
        }
    }

    // =========================================================
    // CARGAR DATOS TÉCNICOS
    // =========================================================

    private void cargarDatosComputadora(Computadora computadora, ComputadoraRequestDTO datos) {
        computadora.setSerie(datos.getSerie());
        computadora.setNombreEquipo(datos.getNombreEquipo());
        computadora.setProcedencia(datos.getProcedencia());
        computadora.setTipo(datos.getTipo());
        computadora.setMarca(datos.getMarca());
        computadora.setModelo(datos.getModelo());
        computadora.setTipoProcesador(datos.getTipoProcesador());
        computadora.setGeneracionProcesador(datos.getGeneracionProcesador());
        computadora.setVelocidadProcesador(datos.getVelocidadProcesador());
        computadora.setMemoriaRAM(datos.getMemoriaRAM());
        computadora.setTipoDisco(datos.getTipoDisco());
        computadora.setCapacidadDiscoGB(datos.getCapacidadDiscoGB());
        computadora.setSistemaOperativo(datos.getSistemaOperativo());
        computadora.setOffice(datos.getOffice());
        computadora.setUbicacion(datos.getUbicacion());
        computadora.setEstado(datos.getEstado());
    }
}
