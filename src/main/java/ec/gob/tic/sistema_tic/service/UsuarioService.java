package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.UsuarioRequestDTO;
import ec.gob.tic.sistema_tic.entity.Usuario;
import ec.gob.tic.sistema_tic.exception.RecursoNoEncontradoException;
import ec.gob.tic.sistema_tic.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // LISTAR TODOS
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // BUSCAR POR ID
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException
                        ("Usuario no encontrado con ID: " + id));
    }

    // BUSCAR POR NOMBRE DE USUARIO
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario);
    }

    // CREAR
    @Transactional
    public Usuario guardar(UsuarioRequestDTO datos) {
        if (datos.getContrasena() == null || datos.getContrasena().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria para nuevos usuarios");
        }

        if (usuarioRepository.findByUsuario(datos.getUsuario()).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(datos.getNombre());
        usuario.setUsuario(datos.getUsuario());
        usuario.setContrasena(passwordEncoder.encode(datos.getContrasena()));
        usuario.setRol(datos.getRol());
        usuario.setEstado(datos.getEstado() == null ? true : datos.getEstado());
        usuario.setFechaCreacion(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    // ACTUALIZAR
    @Transactional
    public Usuario actualizar(Long id, UsuarioRequestDTO datosUsuario) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException
                        ("Usuario no encontrado con ID: " + id));

        // Validar unicidad de username si se modifica
        if (!usuarioExistente.getUsuario().equalsIgnoreCase(datosUsuario.getUsuario())) {
            if (usuarioRepository.findByUsuario(datosUsuario.getUsuario()).isPresent()) {
                throw new IllegalArgumentException("El nombre de usuario ya está en uso");
            }
        }

        usuarioExistente.setNombre(datosUsuario.getNombre());
        usuarioExistente.setUsuario(datosUsuario.getUsuario());
        if (datosUsuario.getContrasena() != null && !datosUsuario.getContrasena().isBlank()) {
            usuarioExistente.setContrasena(passwordEncoder.encode(datosUsuario.getContrasena()));
        }
        usuarioExistente.setRol(datosUsuario.getRol());
        usuarioExistente.setEstado(datosUsuario.getEstado() != null ? datosUsuario.getEstado() : usuarioExistente.isEstado());

        return usuarioRepository.save(usuarioExistente);
    }

    // ELIMINAR (DESACTIVACIÓN LÓGICA)
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException
                        ("Usuario no encontrado con ID: " + id));

        // Desactivación lógica para preservar auditoría y mantenimientos históricos
        usuario.setEstado(false);
        usuarioRepository.save(usuario);
    }
}
