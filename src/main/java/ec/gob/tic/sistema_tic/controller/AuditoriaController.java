package ec.gob.tic.sistema_tic.controller;

import ec.gob.tic.sistema_tic.entity.Auditoria;
import ec.gob.tic.sistema_tic.service.AuditoriaService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;


    public AuditoriaController(
            AuditoriaService auditoriaService) {

        this.auditoriaService =
                auditoriaService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Auditoria>>
    listar() {

        return ResponseEntity.ok(
                auditoriaService.listarUltimas()
        );
    }
}