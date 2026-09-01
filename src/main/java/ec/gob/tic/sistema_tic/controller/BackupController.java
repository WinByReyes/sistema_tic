package ec.gob.tic.sistema_tic.controller;

import ec.gob.tic.sistema_tic.dto.BackupResponseDTO;
import ec.gob.tic.sistema_tic.service.BackupService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/backups")
public class BackupController {

    private final BackupService backupService;


    public BackupController(
            BackupService backupService) {

        this.backupService =
                backupService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Map<String, String>> crearRespaldo() throws Exception {
        String archivo = backupService.crearRespaldo();
        return ResponseEntity.ok(Map.of(
                "mensaje", "Respaldo creado correctamente",
                "archivo", archivo
        ));
    }


// ...

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<BackupResponseDTO>> listarRespaldos() throws Exception {
        return ResponseEntity.ok(backupService.listarRespaldos());
    }
}
