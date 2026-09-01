package ec.gob.tic.sistema_tic.controller;

import ec.gob.tic.sistema_tic.audit.Auditable;
import ec.gob.tic.sistema_tic.dto.CatalogoRequestDTO;
import ec.gob.tic.sistema_tic.entity.Catalogo;
import ec.gob.tic.sistema_tic.service.CatalogoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
public class CatalogoController {

    private final CatalogoService catalogoService;


    public CatalogoController(
            CatalogoService catalogoService) {

        this.catalogoService =
                catalogoService;
    }


    // ==========================================
    // LISTAR ACTIVOS
    //
    // Ejemplo:
    // /api/catalogos/activos/MARCA
    // ==========================================

    @PreAuthorize(
            "hasAnyRole('ADMIN','TECNICO')"
    )
    @GetMapping("/activos/{tipo}")
    public ResponseEntity<List<Catalogo>>
    listarActivos(
            @PathVariable String tipo) {

        return ResponseEntity.ok(
                catalogoService.listarActivos(
                        tipo
                )
        );
    }


    // ==========================================
    // LISTAR TODOS
    // SOLO ADMIN
    // ==========================================

    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    @GetMapping("/{tipo}")
    public ResponseEntity<List<Catalogo>>
    listarTodos(
            @PathVariable String tipo) {

        return ResponseEntity.ok(
                catalogoService.listarTodos(
                        tipo
                )
        );
    }


    // ==========================================
    // BUSCAR
    // ==========================================

    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    @GetMapping("/id/{id}")
    public ResponseEntity<Catalogo>
    buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                catalogoService.buscarPorId(
                        id
                )
        );
    }


    // ==========================================
    // CREAR
    // ==========================================
    @Auditable(
            modulo = "CATALOGOS",
            accion = "CREAR",
            descripcion = "Creó un valor de catálogo"
    )
    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    @PostMapping
    public ResponseEntity<Catalogo>
    crear(
            @Valid @RequestBody
            CatalogoRequestDTO datos) {

        return ResponseEntity.ok(
                catalogoService.crear(
                        datos
                )
        );
    }


    // ==========================================
    // ACTUALIZAR
    // ==========================================
    @Auditable(
            modulo = "CATALOGOS",
            accion = "ACTUALIZAR",
            descripcion = "Actualizó un valor de catálogo"
    )
    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Catalogo>
    actualizar(
            @PathVariable Long id,

            @Valid @RequestBody
            CatalogoRequestDTO datos) {

        return ResponseEntity.ok(
                catalogoService.actualizar(
                        id,
                        datos
                )
        );
    }


    // ==========================================
    // DESACTIVAR
    // ==========================================
    @Auditable(
            modulo = "CATALOGOS",
            accion = "DESACTIVAR",
            descripcion = "Desactivó un valor de catálogo"
    )
    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void>
    desactivar(
            @PathVariable Long id) {

        catalogoService.desactivar(
                id
        );

        return ResponseEntity.noContent()
                .build();
    }


    // ==========================================
    // ACTIVAR
    // ==========================================
    @Auditable(
            modulo = "CATALOGOS",
            accion = "ACTIVAR",
            descripcion = "Activó un valor de catálogo"
    )
    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void>
    activar(
            @PathVariable Long id) {

        catalogoService.activar(
                id
        );

        return ResponseEntity.noContent()
                .build();
    }
}
