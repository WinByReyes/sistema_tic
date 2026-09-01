package ec.gob.tic.sistema_tic.security;

import ec.gob.tic.sistema_tic.entity.Usuario;
import ec.gob.tic.sistema_tic.repository.UsuarioRepository;
import ec.gob.tic.sistema_tic.service.JwtService;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private JwtService jwtService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecretKey key = Keys.hmacShaKeyFor("ClaveSecretaParaPruebasUnitariasSistemaTIC2026_Minimo32Bytes".getBytes(StandardCharsets.UTF_8));
        jwtService = new JwtService(key);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, usuarioRepository);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testUsuarioActivoSeAutentica() throws Exception {
        String token = jwtService.generarToken(1L, "admin", "ADMIN");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        Usuario u = new Usuario();
        u.setUsuario("admin");
        u.setEstado(true);
        when(usuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(u));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("admin", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testUsuarioDesactivadoNoSeAutentica() throws Exception {
        String token = jwtService.generarToken(2L, "admin_inactivo", "ADMIN");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        Usuario u = new Usuario();
        u.setUsuario("admin_inactivo");
        u.setEstado(false); // Inactivo
        when(usuarioRepository.findByUsuario("admin_inactivo")).thenReturn(Optional.of(u));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
