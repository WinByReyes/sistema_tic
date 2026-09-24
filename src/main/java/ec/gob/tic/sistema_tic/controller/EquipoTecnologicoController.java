package ec.gob.tic.sistema_tic.controller;

import ec.gob.tic.sistema_tic.audit.Auditable;
import ec.gob.tic.sistema_tic.dto.EquipoTecnologicoRequestDTO;
import ec.gob.tic.sistema_tic.dto.EquipoTecnologicoResponseDTO;
import ec.gob.tic.sistema_tic.dto.ResumenEquipamientoDTO;
import ec.gob.tic.sistema_tic.service.EquipoTecnologicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos-tecnologicos")
public class EquipoTecnologicoController {

    private final EquipoTecnologicoService equipoTecnologicoService;

    public EquipoTecnologicoController(EquipoTecnologicoService equipoTecnologicoService) {
        this.equipoTecnologicoService = equipoTecnologicoService;
    }

    // GET /api/equipos-tecnologicos
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping
    public List<EquipoTecnologicoResponseDTO> listar(
            @RequestParam(value = "cedula", required = false) String cedula,
            @RequestParam(value = "serie", required = false) String serie,
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "marca", required = false) String marca,
            @RequestParam(value = "modelo", required = false) String modelo,
            @RequestParam(value = "etiqueta", required = false) String etiqueta,
            @RequestParam(value = "nroPR", required = false) String nroPR,
            @RequestParam(value = "ipTelefono", required = false) String ipTelefono) {
        return equipoTecnologicoService.buscar(
                cedula, serie, tipo, marca, modelo, etiqueta, nroPR, ipTelefono);
    }

    // GET /api/equipos-tecnologicos/resumen
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping("/resumen")
    public List<ResumenEquipamientoDTO> resumen() {
        return equipoTecnologicoService.resumenEquipamientoTecnologico();
    }

    // GET /api/equipos-tecnologicos/funcionario/1
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping("/funcionario/{funcionarioId}")
    public List<EquipoTecnologicoResponseDTO> listarPorFuncionario(@PathVariable Long funcionarioId) {
        return equipoTecnologicoService.listarPorFuncionario(funcionarioId);
    }

    // GET /api/equipos-tecnologicos/1
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping("/{id}")
    public ResponseEntity<EquipoTecnologicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(equipoTecnologicoService.buscarPorId(id));
    }

    @Auditable(
            modulo = "EQUIPOS_TECNOLOGICOS",
            accion = "CREAR",
            descripcion = "Registró un equipo tecnológico"
    )
    // POST /api/equipos-tecnologicos
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @PostMapping
    public ResponseEntity<EquipoTecnologicoResponseDTO> crear(
            @Valid @RequestBody EquipoTecnologicoRequestDTO datos) {
        return ResponseEntity.ok(equipoTecnologicoService.guardar(datos));
    }

    @Auditable(
            modulo = "EQUIPOS_TECNOLOGICOS",
            accion = "ACTUALIZAR",
            descripcion = "Actualizó un equipo tecnológico"
    )
    // PUT /api/equipos-tecnologicos/1
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @PutMapping("/{id}")
    public ResponseEntity<EquipoTecnologicoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EquipoTecnologicoRequestDTO datos) {
        return ResponseEntity.ok(equipoTecnologicoService.actualizar(id, datos));
    }

    @Auditable(
            modulo = "EQUIPOS_TECNOLOGICOS",
            accion = "ELIMINAR",
            descripcion = "Eliminó un equipo tecnológico"
    )
    // DELETE /api/equipos-tecnologicos/1
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        equipoTecnologicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}