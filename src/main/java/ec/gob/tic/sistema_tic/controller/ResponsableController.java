package ec.gob.tic.sistema_tic.controller;

import ec.gob.tic.sistema_tic.audit.Auditable;
import ec.gob.tic.sistema_tic.dto.ResponsableRequestDTO;
import ec.gob.tic.sistema_tic.dto.ResponsableResponseDTO;
import ec.gob.tic.sistema_tic.service.ResponsableService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responsables")
public class ResponsableController {

    private final ResponsableService responsableService;

    public ResponsableController(
            ResponsableService responsableService) {

        this.responsableService = responsableService;
    }

    // ==========================================
    // TODOS - SOLO ADMIN
    // ==========================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ResponsableResponseDTO>> listarTodos() {

        return ResponseEntity.ok(
                responsableService.listarTodos()
        );
    }

    // ==========================================
    // ACTIVOS
    // ADMIN Y TECNICO
    // ==========================================

    @PreAuthorize("hasAnyRole('ADMIN','TECNICO')")
    @GetMapping("/activos")
    public ResponseEntity<List<ResponsableResponseDTO>> listarActivos() {

        return ResponseEntity.ok(
                responsableService.listarActivos()
        );
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponsableResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                responsableService.buscarPorId(id)
        );
    }

    // ==========================================
    // CREAR
    // ==========================================
    @Auditable(
            modulo = "RESPONSABLES",
            accion = "CREAR",
            descripcion = "Creó un responsable"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ResponsableResponseDTO> crear(
            @Valid @RequestBody ResponsableRequestDTO responsable) {

        return ResponseEntity.ok(
                responsableService.guardar(responsable)
        );
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================
    @Auditable(
            modulo = "RESPONSABLES",
            accion = "ACTUALIZAR",
            descripcion = "Actualizó un responsable"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponsableResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ResponsableRequestDTO responsable) {

        return ResponseEntity.ok(
                responsableService.actualizar(
                        id,
                        responsable
                )
        );
    }

    // ==========================================
    // DESACTIVAR
    // ==========================================
    @Auditable(
            modulo = "RESPONSABLES",
            accion = "DESACTIVAR",
            descripcion = "Desactivó un responsable"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        responsableService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
