package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.EquipoTecnologicoRequestDTO;
import ec.gob.tic.sistema_tic.dto.EquipoTecnologicoResponseDTO;
import ec.gob.tic.sistema_tic.dto.ResumenEquipamientoDTO;
import ec.gob.tic.sistema_tic.entity.EquipoTecnologico;
import ec.gob.tic.sistema_tic.entity.Funcionario;
import ec.gob.tic.sistema_tic.exception.RecursoNoEncontradoException;
import ec.gob.tic.sistema_tic.repository.EquipoTecnologicoRepository;
import ec.gob.tic.sistema_tic.repository.FuncionarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipoTecnologicoService {

    private final EquipoTecnologicoRepository equipoTecnologicoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public EquipoTecnologicoService(
            EquipoTecnologicoRepository equipoTecnologicoRepository,
            FuncionarioRepository funcionarioRepository) {
        this.equipoTecnologicoRepository = equipoTecnologicoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    // =========================================================
    // LISTAR / BUSCAR
    // =========================================================

    @Transactional(readOnly = true)
    public List<EquipoTecnologicoResponseDTO> listarTodas() {
        return equipoTecnologicoRepository.findAllWithFuncionario()
                .stream()
                .map(EquipoTecnologicoResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EquipoTecnologicoResponseDTO> buscar(
            String cedula,
            String serie,
            String tipo,
            String marca,
            String modelo,
            String etiqueta,
            String nroPR,
            String ipTelefono) {
        String cedulaParam = (cedula != null && !cedula.isBlank()) ? cedula.trim() : "";
        String serieParam = (serie != null && !serie.isBlank()) ? serie.trim() : "";
        String tipoParam = (tipo != null && !tipo.isBlank()) ? tipo.trim() : "";
        String marcaParam = (marca != null && !marca.isBlank()) ? marca.trim() : "";
        String modeloParam = (modelo != null && !modelo.isBlank()) ? modelo.trim() : "";
        String etiquetaParam = (etiqueta != null && !etiqueta.isBlank()) ? etiqueta.trim() : "";
        String nroPRParam = (nroPR != null && !nroPR.isBlank()) ? nroPR.trim() : "";
        String ipTelefonoParam = (ipTelefono != null && !ipTelefono.isBlank()) ? ipTelefono.trim() : "";

        if (cedulaParam.isEmpty() && serieParam.isEmpty() && tipoParam.isEmpty() &&
                marcaParam.isEmpty() && modeloParam.isEmpty() && etiquetaParam.isEmpty() &&
                nroPRParam.isEmpty() && ipTelefonoParam.isEmpty()) {
            return listarTodas();
        }

        validarExisteFuncionario(cedulaParam);

        return equipoTecnologicoRepository.buscarConFiltros(
                        cedulaParam,
                        serieParam,
                        tipoParam,
                        marcaParam,
                        modeloParam,
                        etiquetaParam,
                        nroPRParam,
                        ipTelefonoParam)
                .stream()
                .map(EquipoTecnologicoResponseDTO::new)
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
    public List<EquipoTecnologicoResponseDTO> listarPorFuncionario(Long funcionarioId) {
        return equipoTecnologicoRepository.findByFuncionarioId(funcionarioId)
                .stream()
                .map(EquipoTecnologicoResponseDTO::new)
                .toList();
    }

    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    @Transactional(readOnly = true)
    public EquipoTecnologicoResponseDTO buscarPorId(Long id) {
        EquipoTecnologico equipo = equipoTecnologicoRepository
                .findByIdWithFuncionario(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Equipo tecnológico no encontrado con id: " + id
                        )
                );
        return new EquipoTecnologicoResponseDTO(equipo);
    }

    // =========================================================
    // CREAR EQUIPO TECNOLÓGICO
    // =========================================================

    @Transactional
    public EquipoTecnologicoResponseDTO guardar(EquipoTecnologicoRequestDTO datos) {
        EquipoTecnologico equipo = new EquipoTecnologico();
        cargarDatosEquipo(equipo, datos);

        Funcionario funcionario = buscarFuncionarioOpcional(datos.getFuncionarioId(), datos.getCedulaFuncionario());
        if (funcionario != null) {
            validarFuncionarioActivo(funcionario);
        }

        equipo.setFuncionario(funcionario);
        EquipoTecnologico guardado = equipoTecnologicoRepository.save(equipo);

        return new EquipoTecnologicoResponseDTO(guardado);
    }

    // =========================================================
    // ACTUALIZAR EQUIPO TECNOLÓGICO
    // =========================================================

    @Transactional
    public EquipoTecnologicoResponseDTO actualizar(Long id, EquipoTecnologicoRequestDTO datos) {
        EquipoTecnologico equipo = equipoTecnologicoRepository
                .findByIdWithFuncionario(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Equipo tecnológico no encontrado con ID: " + id
                        )
                );

        Funcionario nuevoFuncionario = buscarFuncionarioOpcional(datos.getFuncionarioId(), datos.getCedulaFuncionario());
        if (nuevoFuncionario != null) {
            validarFuncionarioActivo(nuevoFuncionario);
        }

        cargarDatosEquipo(equipo, datos);
        equipo.setFuncionario(nuevoFuncionario);

        EquipoTecnologico actualizado = equipoTecnologicoRepository.save(equipo);
        return new EquipoTecnologicoResponseDTO(actualizado);
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    @Transactional
    public void eliminar(Long id) {
        EquipoTecnologico equipo = equipoTecnologicoRepository
                .findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Equipo tecnológico no encontrado con ID: " + id
                        )
                );

        equipoTecnologicoRepository.delete(equipo);
    }

    // =========================================================
    // RESUMEN POR TIPO DE EQUIPO
    // =========================================================

    @Transactional(readOnly = true)
    public List<ResumenEquipamientoDTO> resumenEquipamientoTecnologico() {
        return equipoTecnologicoRepository.resumenPorTipoEquipo();
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
                    "Solo se pueden asignar equipos tecnológicos a funcionarios ACTIVO."
            );
        }
    }

    // =========================================================
    // CARGAR DATOS DEL EQUIPO
    // =========================================================

    private void cargarDatosEquipo(EquipoTecnologico equipo, EquipoTecnologicoRequestDTO datos) {
        equipo.setTipoEquipo(datos.getTipoEquipo() != null ? datos.getTipoEquipo().trim() : null);
        equipo.setMarca(datos.getMarca() != null ? datos.getMarca().trim() : null);
        equipo.setModelo(datos.getModelo() != null && !datos.getModelo().isBlank() ? datos.getModelo().trim() : null);
        equipo.setSerie(datos.getSerie() != null && !datos.getSerie().isBlank() ? datos.getSerie().trim() : null);
        equipo.setDetalle(datos.getDetalle() != null && !datos.getDetalle().isBlank() ? datos.getDetalle().trim() : null);
        equipo.setEstado(datos.getEstado() != null ? datos.getEstado().trim() : null);
        equipo.setEtiquetaConstatacion(datos.getEtiquetaConstatacion() != null && !datos.getEtiquetaConstatacion().isBlank()
                ? datos.getEtiquetaConstatacion().trim() : null);
        equipo.setNroPR(datos.getNroPR() != null && !datos.getNroPR().isBlank() ? datos.getNroPR().trim() : null);
        equipo.setIpTelefono(datos.getIpTelefono() != null && !datos.getIpTelefono().isBlank() ? datos.getIpTelefono().trim() : null);
        equipo.setPrecio(datos.getPrecio());
    }
}