package ec.gob.tic.sistema_tic.controller;

import ec.gob.tic.sistema_tic.audit.Auditable;
import ec.gob.tic.sistema_tic.dto.ComputadoraResponseDTO;
import ec.gob.tic.sistema_tic.dto.ComputadoraRequestDTO;
import ec.gob.tic.sistema_tic.service.ComputadoraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/computadoras")
public class ComputadoraController {

    private final ComputadoraService computadoraService;

    public ComputadoraController(ComputadoraService computadoraService) {
        this.computadoraService = computadoraService;
    }

    // GET /api/computadoras
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping
    public List<ComputadoraResponseDTO> listar(
            @RequestParam(value = "serie", required = false) String serie,
            @RequestParam(value = "cedula", required = false) String cedula) {
        if ((serie != null && !serie.isBlank()) || (cedula != null && !cedula.isBlank())) {
            return computadoraService.buscar(serie, cedula);
        }
        return computadoraService.listarTodas();
    }

    // GET /api/computadoras/funcionario/1
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping("/funcionario/{funcionarioId}")
    public List<ComputadoraResponseDTO> listarPorFuncionario(@PathVariable Long funcionarioId) {
        return computadoraService.listarPorFuncionario(funcionarioId);
    }

    // GET /api/computadoras/1
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping("/{id}")
    public ResponseEntity<ComputadoraResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(computadoraService.buscarPorId(id));
    }

    // GET /api/computadoras/serie/ABC123
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping("/serie/{serie}")
    public ResponseEntity<ComputadoraResponseDTO> buscarPorSerie(@PathVariable String serie) {
        return ResponseEntity.ok(computadoraService.buscarPorSerie(serie));
    }

    @Auditable(
            modulo = "COMPUTADORAS",
            accion = "CREAR",
            descripcion = "Creó una computadora"
    )
    // POST /api/computadoras
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @PostMapping
    public ResponseEntity<ComputadoraResponseDTO> crear(
           @Valid @RequestBody ComputadoraRequestDTO datos) {
        return ResponseEntity.ok(computadoraService.guardar(datos));
    }

    @Auditable(
            modulo = "COMPUTADORAS",
            accion = "ACTUALIZAR",
            descripcion = "Actualizó una computadora"
    )
    // PUT /api/computadoras/1
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @PutMapping("/{id}")
    public ResponseEntity<ComputadoraResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ComputadoraRequestDTO datos) {
       return ResponseEntity.ok(computadoraService.actualizar(id, datos));
    }

    @Auditable(
            modulo = "COMPUTADORAS",
            accion = "ELIMINAR",
            descripcion = "Eliminó una computadora"
    )
    // DELETE /api/computadoras/1
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        computadoraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Auditable(
            modulo = "COMPUTADORAS",
            accion = "ASIGNAR",
            descripcion = "Asignó un funcionario a una computadora"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @PatchMapping("/{id}/asignar")
    public ResponseEntity<ComputadoraResponseDTO> asignarFuncionario(
            @PathVariable Long id,
            @RequestParam String cedulaFuncionario) {
        return ResponseEntity.ok(
                computadoraService.asignarFuncionario(id, cedulaFuncionario)
        );
    }

    @Auditable(
            modulo = "COMPUTADORAS",
            accion = "DESASIGNAR",
            descripcion = "Desasignó un funcionario de una computadora"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @PatchMapping("/{id}/desasignar")
    public ResponseEntity<ComputadoraResponseDTO> desasignarFuncionario(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                computadoraService.desasignarFuncionario(id)
        );
    }
}
