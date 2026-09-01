package ec.gob.tic.sistema_tic.controller;

import ec.gob.tic.sistema_tic.audit.Auditable;
import ec.gob.tic.sistema_tic.dto.FuncionarioRequestDTO;
import ec.gob.tic.sistema_tic.dto.FuncionarioResponseDTO;
import ec.gob.tic.sistema_tic.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(
            FuncionarioService funcionarioService) {

        this.funcionarioService = funcionarioService;
    }


    // =========================
    // LISTAR TODOS
    // =========================

    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping
    public ResponseEntity<List<FuncionarioResponseDTO>> listarTodos() {

        return ResponseEntity.ok(
                funcionarioService.listarTodos()
        );
    }


    // =========================
    // BUSCAR POR ID
    // =========================
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                funcionarioService.buscarPorId(id)
        );
    }


    // =========================
    // BUSCAR POR CÉDULA
    // =========================
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<FuncionarioResponseDTO> buscarPorCedula(
            @PathVariable String cedula) {

        return ResponseEntity.ok(
                funcionarioService.buscarPorCedula(cedula)
        );
    }


    // =========================
    // CREAR
    // =========================
    @Auditable(
            modulo = "FUNCIONARIOS",
            accion = "CREAR",
            descripcion = "Creó un funcionario"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @PostMapping
    public ResponseEntity<FuncionarioResponseDTO> crear(
            @Valid @RequestBody FuncionarioRequestDTO datos) {

        return ResponseEntity.ok(
                funcionarioService.guardar(datos)
        );
    }


    // =========================
    // ACTUALIZAR
    // =========================
    @Auditable(
            modulo = "FUNCIONARIOS",
            accion = "ACTUALIZAR",
            descripcion = "Actualizó un funcionario"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody FuncionarioRequestDTO datos) {

        return ResponseEntity.ok(
                funcionarioService.actualizar(id, datos)
        );
    }


    // =========================
    // ELIMINAR
    // =========================
    @Auditable(
            modulo = "FUNCIONARIOS",
            accion = "ELIMINAR",
            descripcion = "Desactivó un funcionario"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        funcionarioService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}