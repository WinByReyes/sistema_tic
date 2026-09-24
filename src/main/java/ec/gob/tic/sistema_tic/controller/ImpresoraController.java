package ec.gob.tic.sistema_tic.controller;

import ec.gob.tic.sistema_tic.audit.Auditable;
import ec.gob.tic.sistema_tic.dto.ImpresoraRequestDTO;
import ec.gob.tic.sistema_tic.dto.ImpresoraResponseDTO;
import ec.gob.tic.sistema_tic.service.ImpresoraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/impresoras")
public class ImpresoraController {

    private final ImpresoraService impresoraService;

    public ImpresoraController(ImpresoraService impresoraService) {
        this.impresoraService = impresoraService;
    }

    // GET /api/impresoras
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping
    public List<ImpresoraResponseDTO> listar(
            @RequestParam(value = "cedula", required = false) String cedula,
            @RequestParam(value = "serie", required = false) String serie,
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "marca", required = false) String marca,
            @RequestParam(value = "modelo", required = false) String modelo) {
        return impresoraService.buscar(cedula, serie, tipo, marca, modelo);
    }

    // GET /api/impresoras/funcionario/1
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping("/funcionario/{funcionarioId}")
    public List<ImpresoraResponseDTO> listarPorFuncionario(@PathVariable Long funcionarioId) {
        return impresoraService.listarPorFuncionario(funcionarioId);
    }

    // GET /api/impresoras/1
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping("/{id}")
    public ResponseEntity<ImpresoraResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(impresoraService.buscarPorId(id));
    }

    @Auditable(
            modulo = "IMPRESORAS",
            accion = "CREAR",
            descripcion = "Registró una impresora"
    )
    // POST /api/impresoras
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @PostMapping
    public ResponseEntity<ImpresoraResponseDTO> crear(
            @Valid @RequestBody ImpresoraRequestDTO datos) {
        return ResponseEntity.ok(impresoraService.guardar(datos));
    }

    @Auditable(
            modulo = "IMPRESORAS",
            accion = "ACTUALIZAR",
            descripcion = "Actualizó una impresora"
    )
    // PUT /api/impresoras/1
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @PutMapping("/{id}")
    public ResponseEntity<ImpresoraResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ImpresoraRequestDTO datos) {
        return ResponseEntity.ok(impresoraService.actualizar(id, datos));
    }

    @Auditable(
            modulo = "IMPRESORAS",
            accion = "ELIMINAR",
            descripcion = "Eliminó una impresora"
    )
    // DELETE /api/impresoras/1
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        impresoraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}