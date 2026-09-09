package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.FuncionarioRequestDTO;
import ec.gob.tic.sistema_tic.dto.FuncionarioResponseDTO;
import ec.gob.tic.sistema_tic.entity.Computadora;
import ec.gob.tic.sistema_tic.entity.Funcionario;
import ec.gob.tic.sistema_tic.exception.RecursoNoEncontradoException;
import ec.gob.tic.sistema_tic.repository.AsignacionComputadoraRepository;
import ec.gob.tic.sistema_tic.repository.ComputadoraRepository;
import ec.gob.tic.sistema_tic.repository.FuncionarioRepository;
import ec.gob.tic.sistema_tic.util.TextoUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final ComputadoraRepository computadoraRepository;
    private final AsignacionComputadoraRepository asignacionRepository;

    public FuncionarioService(
            FuncionarioRepository funcionarioRepository,
            ComputadoraRepository computadoraRepository,
            AsignacionComputadoraRepository asignacionRepository) {

        this.funcionarioRepository = funcionarioRepository;
        this.computadoraRepository = computadoraRepository;
        this.asignacionRepository = asignacionRepository;
    }

    // =========================
    // LISTAR TODOS O BUSCAR
    // =========================

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> listarTodos() {
        return funcionarioRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> buscar(String termino) {
        if (termino == null || termino.isBlank()) {
            return listarTodos();
        }
        return funcionarioRepository.buscarPorTermino(termino.trim())
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // =========================
    // BUSCAR POR ID
    // =========================

    @Transactional(readOnly = true)
    public FuncionarioResponseDTO buscarPorId(Long id) {
        Funcionario funcionario =
                funcionarioRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Funcionario no encontrado con ID: " + id
                                ));

        return convertirAResponse(funcionario);
    }

    // =========================
    // BUSCAR POR CÉDULA
    // =========================

    @Transactional(readOnly = true)
    public FuncionarioResponseDTO buscarPorCedula(String cedula) {
        Funcionario funcionario =
                funcionarioRepository.findByCedula(cedula != null ? cedula.trim() : "")
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Funcionario no encontrado con cédula: " + cedula
                                ));

        return convertirAResponse(funcionario);
    }

    // =========================
    // CREAR
    // =========================

    @Transactional
    public FuncionarioResponseDTO guardar(FuncionarioRequestDTO datos) {
        String cedulaLimpia = TextoUtil.limpiarCedula(datos.getCedula());
        if (cedulaLimpia == null || cedulaLimpia.length() != 10) {
            throw new IllegalArgumentException("La cédula debe tener exactamente 10 dígitos numéricos.");
        }

        funcionarioRepository.findByCedula(cedulaLimpia).ifPresent(f -> {
            throw new IllegalArgumentException("Ya existe un funcionario registrado con la cédula: " + cedulaLimpia);
        });

        if (datos.getCodigoBiometrico() != null && !datos.getCodigoBiometrico().isBlank()) {
            funcionarioRepository.findByCodigoBiometrico(datos.getCodigoBiometrico().trim()).ifPresent(f -> {
                throw new IllegalArgumentException("Ya existe un funcionario con el código biométrico: " + datos.getCodigoBiometrico());
            });
        }

        Funcionario funcionario = new Funcionario();
        funcionario.setCedula(cedulaLimpia);
        aplicarNombresYApellidos(funcionario, datos);
        funcionario.setUnidadAdministrativa(datos.getUnidadAdministrativa() != null ? datos.getUnidadAdministrativa().trim() : "");
        funcionario.setCargo(datos.getCargo() != null ? datos.getCargo().trim() : "");
        funcionario.setEstado(datos.getEstado() != null ? datos.getEstado().trim() : "ACTIVO");
        funcionario.setCodigoBiometrico(datos.getCodigoBiometrico() != null ? datos.getCodigoBiometrico().trim() : "");
        funcionario.setFechaCreacion(LocalDateTime.now());

        Funcionario guardado = funcionarioRepository.save(funcionario);
        return convertirAResponse(guardado);
    }

    // =========================
    // ACTUALIZAR
    // =========================

    @Transactional
    public FuncionarioResponseDTO actualizar(Long id, FuncionarioRequestDTO datos) {
        Funcionario funcionario =
                funcionarioRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Funcionario no encontrado con ID: " + id
                                ));

        String cedulaLimpia = TextoUtil.limpiarCedula(datos.getCedula());
        if (cedulaLimpia == null || cedulaLimpia.length() != 10) {
            throw new IllegalArgumentException("La cédula debe tener exactamente 10 dígitos numéricos.");
        }

        funcionarioRepository.findByCedula(cedulaLimpia).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe otro funcionario con la cédula: " + cedulaLimpia);
            }
        });

        if (datos.getCodigoBiometrico() != null && !datos.getCodigoBiometrico().isBlank()) {
            funcionarioRepository.findByCodigoBiometrico(datos.getCodigoBiometrico().trim()).ifPresent(existente -> {
                if (!existente.getId().equals(id)) {
                    throw new IllegalArgumentException("Ya existe otro funcionario con el código biométrico: " + datos.getCodigoBiometrico());
                }
            });
        }

        funcionario.setCedula(cedulaLimpia);
        aplicarNombresYApellidos(funcionario, datos);
        funcionario.setUnidadAdministrativa(datos.getUnidadAdministrativa() != null ? datos.getUnidadAdministrativa().trim() : "");
        funcionario.setCargo(datos.getCargo() != null ? datos.getCargo().trim() : "");
        funcionario.setEstado(datos.getEstado() != null ? datos.getEstado().trim() : "ACTIVO");
        funcionario.setCodigoBiometrico(datos.getCodigoBiometrico() != null ? datos.getCodigoBiometrico().trim() : "");

        if (!"ACTIVO".equalsIgnoreCase(datos.getEstado())) {
            liberarComputadorasFuncionario(funcionario);
        }

        Funcionario actualizado = funcionarioRepository.save(funcionario);
        return convertirAResponse(actualizado);
    }

    private void aplicarNombresYApellidos(Funcionario funcionario, FuncionarioRequestDTO datos) {
        String nombres = datos.getNombres();
        String apellidos = datos.getApellidos();

        if ((nombres == null || nombres.isBlank()) && datos.getNombrePila() != null) {
            String[] partes = datos.getNombrePila().trim().split("\\s+", 2);
            nombres = partes[0];
            apellidos = partes.length > 1 ? partes[1] : "";
        }

        funcionario.setNombres(TextoUtil.formatoNombre(nombres));
        funcionario.setApellidos(TextoUtil.formatoNombre(apellidos));
    }

    // =========================
    // ELIMINAR (DESACTIVACIÓN LÓGICA)
    // =========================

    @Transactional
    public void eliminar(Long id) {
        Funcionario funcionario =
                funcionarioRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Funcionario no encontrado con ID: " + id
                                ));

        liberarComputadorasFuncionario(funcionario);
        funcionario.setEstado("INACTIVO");
        funcionarioRepository.save(funcionario);
    }

    // =========================
    // LIBERAR COMPUTADORAS
    // =========================

    private void liberarComputadorasFuncionario(Funcionario funcionario) {
        List<Computadora> computadoras =
                computadoraRepository.findByFuncionarioId(funcionario.getId());

        for (Computadora computadora : computadoras) {
            asignacionRepository
                    .findByComputadoraIdAndFechaFinIsNull(computadora.getId())
                    .ifPresent(asignacion -> {
                        asignacion.setFechaFin(LocalDateTime.now());
                        asignacion.setObservaciones(
                                "Asignación finalizada automáticamente por cambio de estado del funcionario: "
                                        + funcionario.getNombreCompleto()
                        );
                        asignacionRepository.save(asignacion);
                    });

            computadora.setFuncionario(null);
            computadoraRepository.save(computadora);
        }
    }

    // =========================
    // CONVERTIR ENTITY → DTO
    // =========================

    public FuncionarioResponseDTO convertirAResponse(Funcionario funcionario) {
        return new FuncionarioResponseDTO(funcionario);
    }
}
