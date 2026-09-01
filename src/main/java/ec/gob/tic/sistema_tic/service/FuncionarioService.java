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
    // LISTAR TODOS
    // =========================

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> listarTodos() {

        return funcionarioRepository.findAll()
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
    public FuncionarioResponseDTO buscarPorCedula(
            String cedula) {

        Funcionario funcionario =
                funcionarioRepository.findByCedula(cedula)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Funcionario no encontrado con cédula: "
                                                + cedula
                                ));

        return convertirAResponse(funcionario);
    }

    // =========================
    // CREAR
    // =========================

    @Transactional
    public FuncionarioResponseDTO guardar(
            FuncionarioRequestDTO datos) {

        Funcionario funcionario = new Funcionario();

        funcionario.setCedula(
                datos.getCedula()
        );

        funcionario.setNombrePila(
                TextoUtil.formatoNombre(
                datos.getNombrePila())
        );

        funcionario.setUnidadAdministrativa(
                datos.getUnidadAdministrativa()
        );

        funcionario.setCargo(
                datos.getCargo()
        );

        funcionario.setEstado(
                datos.getEstado()
        );

        funcionario.setCodigoBiometrico(
                datos.getCodigoBiometrico()
        );

        funcionario.setFechaCreacion(
                LocalDateTime.now()
        );

        Funcionario guardado =
                funcionarioRepository.save(
                        funcionario
                );

        return convertirAResponse(
                guardado
        );
    }

    // =========================
    // ACTUALIZAR
    // =========================

    @Transactional
    public FuncionarioResponseDTO actualizar(
            Long id,
            FuncionarioRequestDTO datos) {

        Funcionario funcionario =
                funcionarioRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Funcionario no encontrado con ID: "
                                                + id
                                ));

        // =========================
        // ACTUALIZAR DATOS
        // =========================

        funcionario.setCedula(
                datos.getCedula()
        );

        funcionario.setNombrePila(
                TextoUtil.formatoNombre(
                datos.getNombrePila())
        );

        funcionario.setUnidadAdministrativa(
                datos.getUnidadAdministrativa()
        );

        funcionario.setCargo(
                datos.getCargo()
        );

        funcionario.setEstado(
                datos.getEstado()
        );

        funcionario.setCodigoBiometrico(
                datos.getCodigoBiometrico()
        );


        // =========================
        // LIBERAR COMPUTADORAS
        // =========================
        //
        // Solamente los funcionarios
        // ACTIVO pueden tener computadoras.
        //
        // Si cambia a:
        // - INACTIVO
        // - JUBILADO
        // - CESADO
        //
        // sus computadoras quedan
        // automáticamente SIN ASIGNAR.
        // =========================

        if (!"ACTIVO".equalsIgnoreCase(
                datos.getEstado())) {

            liberarComputadorasFuncionario(
                    funcionario
            );
        }


        // =========================
        // GUARDAR FUNCIONARIO
        // =========================

        Funcionario actualizado =
                funcionarioRepository.save(
                        funcionario
                );

        return convertirAResponse(
                actualizado
        );
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
                                        "Funcionario no encontrado con ID: "
                                                + id
                                ));

        // Liberar computadoras asignadas
        liberarComputadorasFuncionario(funcionario);

        // Desactivación lógica para preservar la trazabilidad institucional
        funcionario.setEstado("INACTIVO");
        funcionarioRepository.save(funcionario);
    }

    // =========================
    // LIBERAR COMPUTADORAS
    // =========================

    private void liberarComputadorasFuncionario(
            Funcionario funcionario) {

        List<Computadora> computadoras =
                computadoraRepository
                        .findByFuncionarioId(
                                funcionario.getId()
                        );

        for (Computadora computadora :
                computadoras) {

            // =========================
            // CERRAR ASIGNACIÓN ACTUAL
            // =========================

            asignacionRepository
                    .findByComputadoraIdAndFechaFinIsNull(
                            computadora.getId()
                    )
                    .ifPresent(asignacion -> {

                        asignacion.setFechaFin(
                            LocalDateTime.now()
                        );

                        asignacion.setObservaciones(
                                "Asignación finalizada automáticamente "
                                        + "por cambio de estado del funcionario: "
                                        + funcionario.getNombrePila()
                        );

                        asignacionRepository.save(
                                asignacion
                        );
                    });


            // =========================
            // DEJAR COMPUTADORA SIN FUNCIONARIO
            // =========================

            computadora.setFuncionario(
                    null
            );

            computadoraRepository.save(
                    computadora
            );
        }
    }

    // =========================
    // CONVERTIR ENTITY → DTO
    // =========================

    private FuncionarioResponseDTO convertirAResponse(
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
                funcionario.getUnidadAdministrativa()
        );

        dto.setCargo(
                funcionario.getCargo()
        );

        dto.setEstado(
                funcionario.getEstado()
        );

        dto.setCodigoBiometrico(
                funcionario.getCodigoBiometrico()
        );

        dto.setFechaCreacion(
                funcionario.getFechaCreacion()
        );

        return dto;
    }
}
