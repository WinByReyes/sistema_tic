package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.config.PasswordConfig;
import ec.gob.tic.sistema_tic.dto.UsuarioRequestDTO;
import ec.gob.tic.sistema_tic.entity.Usuario;
import ec.gob.tic.sistema_tic.exception.RecursoNoEncontradoException;
import ec.gob.tic.sistema_tic.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        usuarioDTO = new UsuarioRequestDTO();
        usuarioDTO.setNombre("Admin General");
        usuarioDTO.setUsuario("admin_test");
        usuarioDTO.setContrasena("123456");
        usuarioDTO.setRol("ADMIN");
        usuarioDTO.setEstado(true);
    }

    @Test
    void testGuardarUsuarioExitoso() {
        when(usuarioRepository.findByUsuario("admin_test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        Usuario creado = usuarioService.guardar(usuarioDTO);

        assertNotNull(creado);
        assertEquals("admin_test", creado.getUsuario());
        assertEquals("hashedPassword", creado.getContrasena());
        assertTrue(creado.isEstado());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testGuardarUsuarioDuplicadoLanzaExcepcion() {
        when(usuarioRepository.findByUsuario("admin_test")).thenReturn(Optional.of(new Usuario()));

        assertThrows(IllegalArgumentException.class, () -> usuarioService.guardar(usuarioDTO));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testEliminarUsuarioDesactivacionLogica() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEstado(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.eliminar(1L);

        assertFalse(usuario.isEstado());
        verify(usuarioRepository, times(1)).save(usuario);
        verify(usuarioRepository, never()).delete(any());
    }
}
