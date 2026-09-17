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
    // LISTAR / BUSCAR
    // =========================================================

    @Transactional(readOnly = true)
    public List<ComputadoraResponseDTO> listarTodas() {
        return computadoraRepository.findAllWithFuncionario()
                .stream()
                .map(ComputadoraResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ComputadoraResponseDTO> listarSinFuncionario() {
        return computadoraRepository.findSinFuncionario()
                .stream()
                .map(ComputadoraResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ComputadoraResponseDTO> buscar(String serie, String cedula) {
        String serieParam = (serie != null && !serie.isBlank()) ? serie.trim() : "";
        String cedulaParam = (cedula != null && !cedula.isBlank()) ? cedula.trim() : "";

        if (serieParam.isEmpty() && cedulaParam.isEmpty()) {
            return listarTodas();
        }

        validarExisteFuncionario(cedulaParam);

        return computadoraRepository.buscarPorSerieYCedula(serieParam, cedulaParam)
                .stream()
                .map(ComputadoraResponseDTO::new)
                .toList();
    }

    private void validarExisteFuncionario(String cedula) {
        if (cedula == null || cedula.isEmpty()) {
            return;
        }
        if (funcionarioRepository.findByCedulaContaining(cedula).isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "No existe un funcionario con la cédula: " + cedula
            );
        }
    }

    @Transactional(readOnly = true)
    public List<ComputadoraResponseDTO> listarPorFuncionario(Long funcionarioId) {
        return computadoraRepository.findByFuncionarioId(funcionarioId)
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
                .findBySerieWithFuncionario(serie != null ? serie.trim() : "")
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
        if (datos.getSerie() != null) {
            computadoraRepository.findBySerie(datos.getSerie().trim()).ifPresent(c -> {
                throw new IllegalArgumentException("Ya existe una computadora registrada con la serie: " + datos.getSerie());
            });
        }

        Computadora computadora = new Computadora();
        cargarDatosComputadora(computadora, datos);

        Funcionario funcionario = buscarFuncionarioOpcional(datos.getFuncionarioId(), datos.getCedulaFuncionario());
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

        if (datos.getSerie() != null) {
            computadoraRepository.findBySerie(datos.getSerie().trim()).ifPresent(existente -> {
                if (!existente.getId().equals(id)) {
                    throw new IllegalArgumentException("Ya existe otra computadora con la serie: " + datos.getSerie());
                }
            });
        }

        Funcionario funcionarioAnterior = computadora.getFuncionario();
        Funcionario nuevoFuncionario = buscarFuncionarioOpcional(datos.getFuncionarioId(), datos.getCedulaFuncionario());

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
                .findByCedula(cedulaFuncionario.trim())
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

    private Funcionario buscarFuncionarioOpcional(Long funcionarioId, String cedula) {
        if (funcionarioId != null) {
            return funcionarioRepository.findById(funcionarioId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Funcionario no encontrado con ID: " + funcionarioId));
        }

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
        computadora.setSerie(datos.getSerie() != null ? datos.getSerie().trim() : null);
        computadora.setNombreEquipo(datos.getNombreEquipo() != null ? datos.getNombreEquipo().trim() : null);
        computadora.setProcedencia(datos.getProcedencia() != null && !datos.getProcedencia().isBlank() ? datos.getProcedencia().trim() : "Institucional");
        computadora.setTipo(datos.getTipo() != null ? datos.getTipo().trim() : null);
        computadora.setMarca(datos.getMarca() != null ? datos.getMarca().trim() : null);
        computadora.setModelo(datos.getModelo() != null ? datos.getModelo().trim() : null);
        computadora.setTipoProcesador(datos.getTipoProcesador() != null ? datos.getTipoProcesador().trim() : null);
        computadora.setGeneracionProcesador(datos.getGeneracionProcesador() != null ? datos.getGeneracionProcesador().trim() : null);
        computadora.setVelocidadProcesador(datos.getVelocidadProcesador() != null && !datos.getVelocidadProcesador().isBlank() ? datos.getVelocidadProcesador().trim() : "N/A");
        computadora.setMemoriaRAM(datos.getMemoriaRAM() != null ? datos.getMemoriaRAM().trim() : null);
        computadora.setTipoDisco(datos.getTipoDisco() != null ? datos.getTipoDisco().trim() : null);
        computadora.setCapacidadDiscoGB(datos.getCapacidadDiscoGB());
        computadora.setSistemaOperativo(datos.getSistemaOperativo() != null ? datos.getSistemaOperativo().trim() : null);
        
        String ofimatica = datos.getOffice() != null && !datos.getOffice().isBlank() ? datos.getOffice().trim() : datos.getOfimatica();
        computadora.setOffice(ofimatica);
        computadora.setAntivirus(datos.getAntivirus() != null ? datos.getAntivirus().trim() : null);
        computadora.setObservacionSoftware(datos.getObservacionSoftware());

        computadora.setIp(datos.getIp() != null ? datos.getIp().trim() : null);
        computadora.setMacLan(datos.getMacLan() != null ? datos.getMacLan().trim() : null);
        computadora.setMacWifi(datos.getMacWifi() != null ? datos.getMacWifi().trim() : null);
        computadora.setNroPR(datos.getNroPR() != null ? datos.getNroPR().trim() : null);
        computadora.setObservacionRed(datos.getObservacionRed());

        computadora.setUbicacion(datos.getUbicacion() != null ? datos.getUbicacion().trim() : null);
        computadora.setEstado(datos.getEstado() != null ? datos.getEstado().trim() : null);
    }
}
