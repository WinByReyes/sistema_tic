package ec.gob.tic.sistema_tic.controller;

import ec.gob.tic.sistema_tic.dto.InventarioItemDTO;
import ec.gob.tic.sistema_tic.dto.InventarioResumenDTO;
import ec.gob.tic.sistema_tic.service.InventarioService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    // GET /api/inventario/general
    // Tabla consolidada y ordenada alfabéticamente con todas las
    // computadoras, periféricos e impresoras registradas.
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping("/general")
    public List<InventarioItemDTO> general() {
        return inventarioService.listarInventarioGeneral();
    }

    // GET /api/inventario/resumen?anio=2026&desde=2026-01-01&hasta=2026-12-31
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @GetMapping("/resumen")
    public InventarioResumenDTO resumen(
            @RequestParam(name = "anio", required = false) Integer anio,
            @RequestParam(name = "desde", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(name = "hasta", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return inventarioService.obtenerResumen(anio, desde, hasta);
    }
}