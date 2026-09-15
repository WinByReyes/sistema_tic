package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.MantenimientoRequestDTO;
import ec.gob.tic.sistema_tic.dto.MantenimientoResponseDTO;
import ec.gob.tic.sistema_tic.entity.Computadora;
import ec.gob.tic.sistema_tic.entity.Mantenimiento;
import ec.gob.tic.sistema_tic.entity.Usuario;
import ec.gob.tic.sistema_tic.exception.RecursoNoEncontradoException;
import ec.gob.tic.sistema_tic.repository.ComputadoraRepository;
import ec.gob.tic.sistema_tic.repository.FuncionarioRepository;
import ec.gob.tic.sistema_tic.repository.MantenimientoRepository;
import ec.gob.tic.sistema_tic.repository.UsuarioRepository;
import ec.gob.tic.sistema_tic.util.TextoUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final ComputadoraRepository computadoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final CatalogoService catalogoService;
    private final FuncionarioRepository funcionarioRepository;

    public MantenimientoService(
            MantenimientoRepository mantenimientoRepository,
            ComputadoraRepository computadoraRepository,
            UsuarioRepository usuarioRepository,
            CatalogoService catalogoService,
            FuncionarioRepository funcionarioRepository
    ) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.computadoraRepository = computadoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.catalogoService = catalogoService;
        this.funcionarioRepository = funcionarioRepository;
    }

    // ==========================================
    // GUARDAR / CREAR MANTENIMIENTO
    // ==========================================

    @Transactional
    public MantenimientoResponseDTO guardar(MantenimientoRequestDTO datos) {
        return crear(datos);
    }

    @Transactional
    public MantenimientoResponseDTO crear(MantenimientoRequestDTO datos) {
        validarCatalogos(datos);

        Computadora computadora = computadoraRepository.findById(datos.getComputadoraId())
                .orElseThrow(() -> new RecursoNoEncontradoException("La computadora indicada no existe"));

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();

        Mantenimiento mantenimiento = new Mantenimiento();
        mantenimiento.setComputadora(computadora);
        mantenimiento.setUsuario(usuarioAutenticado);
        mantenimiento.setFechaMantenimiento(
                datos.getFechaMantenimiento() != null
                        ? datos.getFechaMantenimiento()
                        : LocalDateTime.now()
        );
        mantenimiento.setTipoMantenimiento(datos.getTipoMantenimiento());
        mantenimiento.setEstadoMantenimiento(datos.getEstadoMantenimiento());
        mantenimiento.setResponsable(datos.getResponsable());
        mantenimiento.setDiagnostico(TextoUtil.limpiar(datos.getDiagnostico()));
        mantenimiento.setTrabajoRealizado(TextoUtil.limpiar(datos.getTrabajoRealizado()));
        mantenimiento.setCosto(datos.getCosto() != null ? datos.getCosto() : BigDecimal.ZERO);


        // Guardar estado posterior
        mantenimiento.setEstadoPosterior(datos.getEstadoPosterior());

        // Actualizar el estado actual de la computadora de manera transaccional
        if (datos.getEstadoPosterior() != null && !datos.getEstadoPosterior().trim().isEmpty()) {
            computadora.setEstado(datos.getEstadoPosterior());
            computadoraRepository.save(computadora);
        }

        mantenimiento.setObservaciones(TextoUtil.limpiar(datos.getObservaciones()));

        Mantenimiento guardado = mantenimientoRepository.save(mantenimiento);
        return new MantenimientoResponseDTO(guardado);
    }

    // ==========================================
    // ACTUALIZAR MANTENIMIENTO
    // ==========================================

    @Transactional
    public MantenimientoResponseDTO actualizar(Long id, MantenimientoRequestDTO datos) {
        validarCatalogos(datos);

        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El mantenimiento no existe"));

        Computadora computadora = computadoraRepository.findById(datos.getComputadoraId())
                .orElseThrow(() -> new RecursoNoEncontradoException("La computadora indicada no existe"));

        mantenimiento.setComputadora(computadora);
        if (datos.getFechaMantenimiento() != null) {
            mantenimiento.setFechaMantenimiento(datos.getFechaMantenimiento());
        }
        mantenimiento.setTipoMantenimiento(datos.getTipoMantenimiento());
        mantenimiento.setEstadoMantenimiento(datos.getEstadoMantenimiento());
        mantenimiento.setResponsable(datos.getResponsable());
        mantenimiento.setDiagnostico(TextoUtil.limpiar(datos.getDiagnostico()));
        mantenimiento.setTrabajoRealizado(TextoUtil.limpiar(datos.getTrabajoRealizado()));
        mantenimiento.setCosto(datos.getCosto() != null ? datos.getCosto() : BigDecimal.ZERO);

        mantenimiento.setEstadoPosterior(datos.getEstadoPosterior());

        // Actualizar el estado actual de la computadora de manera transaccional
        if (datos.getEstadoPosterior() != null && !datos.getEstadoPosterior().trim().isEmpty()) {
            computadora.setEstado(datos.getEstadoPosterior());
            computadoraRepository.save(computadora);
        }

        mantenimiento.setObservaciones(TextoUtil.limpiar(datos.getObservaciones()));

        Mantenimiento guardado = mantenimientoRepository.save(mantenimiento);
        return new MantenimientoResponseDTO(guardado);
    }


    // ==========================================
    // LISTAR / BUSCAR
    // ==========================================

    @Transactional(readOnly = true)
    public List<MantenimientoResponseDTO> listarTodos() {
        return mantenimientoRepository.findAllWithDetails()
                .stream()
                .map(MantenimientoResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MantenimientoResponseDTO> buscar(String serie, String cedula) {
        String serieParam = (serie != null && !serie.trim().isEmpty()) ? serie.trim() : null;
        String cedulaParam = (cedula != null && !cedula.trim().isEmpty()) ? cedula.trim() : null;

        validarExisteFuncionario(cedulaParam);

        return mantenimientoRepository.buscarPorSerieYCedula(serieParam, cedulaParam)
                .stream()
                .map(MantenimientoResponseDTO::new)
                .toList();
    }

    private void validarExisteFuncionario(String cedula) {
        if (cedula == null) {
            return;
        }
        if (funcionarioRepository.findByCedulaContainingIgnoreCase(cedula).isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "No existe un funcionario con la cédula: " + cedula
            );
        }
    }

    @Transactional(readOnly = true)
    public MantenimientoResponseDTO buscarPorId(Long id) {
        return obtenerPorId(id);
    }

    @Transactional(readOnly = true)
    public MantenimientoResponseDTO obtenerPorId(Long id) {
        Mantenimiento mantenimiento = mantenimientoRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El mantenimiento no existe"));
        return new MantenimientoResponseDTO(mantenimiento);
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    @Transactional
    public void eliminar(Long id) {
        if (!mantenimientoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("El mantenimiento no existe");
        }
        mantenimientoRepository.deleteById(id);
    }

    // ==========================================
    // VALIDACIONES Y AUXILIARES
    // ==========================================

    private void validarCatalogos(MantenimientoRequestDTO datos) {

        catalogoService.validarValor(
                "RESPONSABLE_MANTENIMIENTO",
                datos.getResponsable()
        );

        catalogoService.validarValor(
                "TIPO_MANTENIMIENTO",
                datos.getTipoMantenimiento()
        );

        catalogoService.validarValor(
                "ESTADO_MANTENIMIENTO",
                datos.getEstadoMantenimiento()
        );

        if (datos.getEstadoPosterior() != null &&
                !datos.getEstadoPosterior().trim().isEmpty()) {

            catalogoService.validarValor(
                    "ESTADO_POSTERIOR_COMPUTADORA",
                    datos.getEstadoPosterior()
            );
        }
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            return usuarioRepository.findByUsuario(username).orElse(null);
        }
        return null;
    }
}
