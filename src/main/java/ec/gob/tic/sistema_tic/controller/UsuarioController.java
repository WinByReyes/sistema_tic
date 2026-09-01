package ec.gob.tic.sistema_tic.controller;

import ec.gob.tic.sistema_tic.audit.Auditable;
import ec.gob.tic.sistema_tic.dto.UsuarioRequestDTO;
import ec.gob.tic.sistema_tic.dto.UsuarioResponseDTO;
import ec.gob.tic.sistema_tic.entity.Usuario;
import ec.gob.tic.sistema_tic.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // =========================
    // LISTAR TODOS
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UsuarioResponseDTO> listarUsuarios() {

        return usuarioService.listarTodos().
                stream()
                .map(UsuarioResponseDTO::new)
                .toList();
    }


    // =========================
    // BUSCAR POR ID
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {

        Usuario usuario = usuarioService.buscarPorId(id);

        return ResponseEntity.ok(new UsuarioResponseDTO(usuario));
    }


    // =========================
    // CREAR
    // =========================
    @Auditable(
            modulo = "USUARIOS",
            accion = "CREAR",
            descripcion = "Creó un usuario"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody UsuarioRequestDTO usuario) {

        Usuario nuevoUsuario = usuarioService.guardar(usuario);

        return ResponseEntity.ok(new UsuarioResponseDTO(nuevoUsuario));
    }


    // =========================
    // ACTUALIZAR
    // =========================
    @Auditable(
            modulo = "USUARIOS",
            accion = "ACTUALIZAR",
            descripcion = "Actualizó un usuario"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario( @PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO usuario) {

        Usuario usuarioactualizado = usuarioService.actualizar(id, usuario);

        return ResponseEntity.ok(new UsuarioResponseDTO(usuarioactualizado));
    }


    // =========================
    // ELIMINAR
    // =========================
    @Auditable(
            modulo = "USUARIOS",
            accion = "ELIMINAR",
            descripcion = "Desactivó un usuario"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {

        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
