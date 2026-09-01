package ec.gob.tic.sistema_tic.controller;

import ec.gob.tic.sistema_tic.audit.Auditable;
import ec.gob.tic.sistema_tic.dto.ConfiguracionInstitucionalDTO;
import ec.gob.tic.sistema_tic.service.ConfiguracionInstitucionalService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracion-institucional")
public class ConfiguracionInstitucionalController {

    private final ConfiguracionInstitucionalService service;


    public ConfiguracionInstitucionalController(
            ConfiguracionInstitucionalService service) {

        this.service =
                service;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ConfiguracionInstitucionalDTO>
    obtener() {

        return ResponseEntity.ok(
                service.obtener()
        );
    }


    @Auditable(
            modulo = "CONFIGURACION",
            accion = "ACTUALIZAR",
            descripcion =
                    "Actualizó la configuración institucional"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<ConfiguracionInstitucionalDTO>
    actualizar(
            @Valid @RequestBody
            ConfiguracionInstitucionalDTO datos) {

        return ResponseEntity.ok(
                service.actualizar(datos)
        );
    }
}
