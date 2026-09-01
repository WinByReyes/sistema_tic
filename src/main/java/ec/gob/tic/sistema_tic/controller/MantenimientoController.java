package ec.gob.tic.sistema_tic.controller;

import ec.gob.tic.sistema_tic.audit.Auditable;
import ec.gob.tic.sistema_tic.dto.MantenimientoRequestDTO;
import ec.gob.tic.sistema_tic.dto.MantenimientoResponseDTO;
import ec.gob.tic.sistema_tic.entity.Mantenimiento;
import ec.gob.tic.sistema_tic.service.MantenimientoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    public MantenimientoController(
            MantenimientoService mantenimientoService) {

        this.mantenimientoService = mantenimientoService;
    }

    // GET /api/mantenimientos
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping
    public ResponseEntity<List<MantenimientoResponseDTO>> listarTodos() {

        return ResponseEntity.ok(
                mantenimientoService.listarTodos()
        );
    }

    // GET /api/mantenimientos/1
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping("/{id}")
    public ResponseEntity<MantenimientoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(mantenimientoService.buscarPorId(id)
        );
    }

    // POST /api/mantenimientos
    @Auditable(
            modulo = "MANTENIMIENTOS",
            accion = "CREAR",
            descripcion = "Registró un mantenimiento"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @PostMapping
    public ResponseEntity<MantenimientoResponseDTO> crear(
            @Valid @RequestBody MantenimientoRequestDTO datos) {

        return ResponseEntity.ok(
                mantenimientoService.guardar(datos)
        );
    }

    //PUT /api/mantenimientos
    @Auditable(
            modulo = "MANTENIMIENTOS",
            accion = "ACTUALIZAR",
            descripcion = "Actualizó un mantenimiento"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @PutMapping("/{id}")
    public ResponseEntity<MantenimientoResponseDTO> actualizar
            (@PathVariable Long id,
             @Valid @RequestBody MantenimientoRequestDTO datos)
    {
        return ResponseEntity.ok(mantenimientoService.actualizar(id, datos));
    }

    //Delete
    @Auditable(
            modulo = "MANTENIMIENTOS",
            accion = "ELIMINAR",
            descripcion = "Eliminó un mantenimiento"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id)
    {
        mantenimientoService.eliminar(id);
        return ResponseEntity.noContent().build();

    }
}