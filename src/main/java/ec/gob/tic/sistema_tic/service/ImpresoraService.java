package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.ImpresoraRequestDTO;
import ec.gob.tic.sistema_tic.dto.ImpresoraResponseDTO;
import ec.gob.tic.sistema_tic.entity.Funcionario;
import ec.gob.tic.sistema_tic.entity.Impresora;
import ec.gob.tic.sistema_tic.exception.RecursoNoEncontradoException;
import ec.gob.tic.sistema_tic.repository.FuncionarioRepository;
import ec.gob.tic.sistema_tic.repository.ImpresoraRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImpresoraService {

    private final ImpresoraRepository impresoraRepository;
    private final FuncionarioRepository funcionarioRepository;

    public ImpresoraService(
            ImpresoraRepository impresoraRepository,
            FuncionarioRepository funcionarioRepository) {
        this.impresoraRepository = impresoraRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    // =========================================================
    // LISTAR / BUSCAR
    // =========================================================

    @Transactional(readOnly = true)
    public List<ImpresoraResponseDTO> listarTodas() {
        return impresoraRepository.findAllWithFuncionario()
                .stream()
                .map(ImpresoraResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ImpresoraResponseDTO> buscar(
            String cedula,
            String serie,
            String tipo,
            String marca,
            String modelo) {
        String cedulaParam = (cedula != null && !cedula.isBlank()) ? cedula.trim() : "";
        String serieParam = (serie != null && !serie.isBlank()) ? serie.trim() : "";
        String tipoParam = (tipo != null && !tipo.isBlank()) ? tipo.trim() : "";
        String marcaParam = (marca != null && !marca.isBlank()) ? marca.trim() : "";
        String modeloParam = (modelo != null && !modelo.isBlank()) ? modelo.trim() : "";

        if (cedulaParam.isEmpty() && serieParam.isEmpty() && tipoParam.isEmpty() &&
                marcaParam.isEmpty() && modeloParam.isEmpty()) {
            return listarTodas();
        }

        validarExisteFuncionario(cedulaParam);

        return impresoraRepository.buscarConFiltros(
                        cedulaParam,
                        serieParam,
                        tipoParam,
                        marcaParam,
                        modeloParam)
                .stream()
                .map(ImpresoraResponseDTO::new)
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
    public List<ImpresoraResponseDTO> listarPorFuncionario(Long funcionarioId) {
        return impresoraRepository.findByFuncionarioId(funcionarioId)
                .stream()
                .map(ImpresoraResponseDTO::new)
                .toList();
    }

    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    @Transactional(readOnly = true)
    public ImpresoraResponseDTO buscarPorId(Long id) {
        Impresora impresora = impresoraRepository
                .findByIdWithFuncionario(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Impresora no encontrada con id: " + id
                        )
                );
        return new ImpresoraResponseDTO(impresora);
    }

    // =========================================================
    // CREAR IMPRESORA
    // =========================================================

    @Transactional
    public ImpresoraResponseDTO guardar(ImpresoraRequestDTO datos) {
        Impresora impresora = new Impresora();
        cargarDatosImpresora(impresora, datos);

        Funcionario funcionario = buscarFuncionarioOpcional(datos.getFuncionarioId(), datos.getCedulaFuncionario());
        if (funcionario != null) {
            validarFuncionarioActivo(funcionario);
        }

        impresora.setFuncionario(funcionario);
        Impresora guardada = impresoraRepository.save(impresora);

        return new ImpresoraResponseDTO(guardada);
    }

    // =========================================================
    // ACTUALIZAR IMPRESORA
    // =========================================================

    @Transactional
    public ImpresoraResponseDTO actualizar(Long id, ImpresoraRequestDTO datos) {
        Impresora impresora = impresoraRepository
                .findByIdWithFuncionario(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Impresora no encontrada con ID: " + id
                        )
                );

        Funcionario nuevoFuncionario = buscarFuncionarioOpcional(datos.getFuncionarioId(), datos.getCedulaFuncionario());
        if (nuevoFuncionario != null) {
            validarFuncionarioActivo(nuevoFuncionario);
        }

        cargarDatosImpresora(impresora, datos);
        impresora.setFuncionario(nuevoFuncionario);

        Impresora actualizada = impresoraRepository.save(impresora);
        return new ImpresoraResponseDTO(actualizada);
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    @Transactional
    public void eliminar(Long id) {
        Impresora impresora = impresoraRepository
                .findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Impresora no encontrada con ID: " + id
                        )
                );

        impresoraRepository.delete(impresora);
    }

    // =========================================================
    // BUSCAR FUNCIONARIO OPCIONAL
    // =========================================================

    private Funcionario buscarFuncionarioOpcional(Long funcionarioId, String cedula) {
        if (funcionarioId != null) {
            return funcionarioRepository.findById(funcionarioId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Funcionario no encontrado con ID: " + funcionarioId));
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
                    "Solo se pueden asignar impresoras a funcionarios ACTIVO."
            );
        }
    }

    // =========================================================
    // CARGAR DATOS DE LA IMPRESORA
    // =========================================================

    private void cargarDatosImpresora(Impresora impresora, ImpresoraRequestDTO datos) {
        impresora.setTipoEquipo(datos.getTipoEquipo() != null ? datos.getTipoEquipo().trim() : null);
        impresora.setMarca(datos.getMarca() != null ? datos.getMarca().trim() : null);
        impresora.setModelo(datos.getModelo() != null && !datos.getModelo().isBlank() ? datos.getModelo().trim() : null);
        impresora.setSerie(datos.getSerie() != null && !datos.getSerie().isBlank() ? datos.getSerie().trim() : null);
        impresora.setEstado(datos.getEstado() != null ? datos.getEstado().trim() : null);
    }
}