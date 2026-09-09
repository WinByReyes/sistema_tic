package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.MantenimientoRequestDTO;
import ec.gob.tic.sistema_tic.dto.MantenimientoResponseDTO;
import ec.gob.tic.sistema_tic.entity.Computadora;
import ec.gob.tic.sistema_tic.entity.Mantenimiento;
import ec.gob.tic.sistema_tic.entity.Usuario;
import ec.gob.tic.sistema_tic.repository.CatalogoRepository;
import ec.gob.tic.sistema_tic.repository.ComputadoraRepository;
import ec.gob.tic.sistema_tic.repository.MantenimientoRepository;
import ec.gob.tic.sistema_tic.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MantenimientoServiceTest {

    @Mock
    private MantenimientoRepository mantenimientoRepository;

    @Mock
    private ComputadoraRepository computadoraRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CatalogoRepository catalogoRepository;

    private CatalogoService catalogoService;
    private MantenimientoService mantenimientoService;

    private Computadora computadora;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        catalogoService = new CatalogoService(catalogoRepository);
        mantenimientoService = new MantenimientoService(
                mantenimientoRepository,
                computadoraRepository,
                usuarioRepository,
                catalogoService
        );

        computadora = new Computadora();
        computadora.setId(1L);
        computadora.setSerie("SN123456");
        computadora.setNombreEquipo("PC-01");
        computadora.setEstado("BUENO");

        usuario = new Usuario();
        usuario.setId(10L);
        usuario.setUsuario("admin");
        usuario.setNombre("Admin TIC");

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "admin",
                "pass",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGuardarConResponsableDelCatalogo() {
        MantenimientoRequestDTO dto = new MantenimientoRequestDTO();
        dto.setComputadoraId(1L);
        dto.setResponsable("Soporte Externo Acme");
        dto.setTipoMantenimiento("TIPO_MANTENIMIENTO");
        dto.setEstadoMantenimiento("ESTADO_MANTENIMIENTO");
        dto.setDiagnostico("Revision semestral");
        dto.setTrabajoRealizado("Limpieza y actualizacion");
        dto.setCosto(BigDecimal.ZERO);
        dto.setEstadoPosterior("EXCELENTE");
        dto.setObservaciones("Equipo operativo");
        dto.setFechaMantenimiento(LocalDateTime.now());

        when(computadoraRepository.findById(1L)).thenReturn(Optional.of(computadora));
        when(usuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(usuario));
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenAnswer(i -> {
            Mantenimiento m = i.getArgument(0);
            m.setId(100L);
            return m;
        });

        MantenimientoResponseDTO response = mantenimientoService.guardar(dto);

        assertNotNull(response);
        assertEquals("Soporte Externo Acme", response.getNombreResponsable());
        assertEquals("EXCELENTE", computadora.getEstado());
        verify(mantenimientoRepository, times(1)).save(any(Mantenimiento.class));
    }

    @Test
    void testGuardarConResponsablePorDefecto() {
        MantenimientoRequestDTO dto = new MantenimientoRequestDTO();
        dto.setComputadoraId(1L);
        dto.setResponsable("Responsable asignado");
        dto.setTipoMantenimiento("TIPO_MANTENIMIENTO");
        dto.setEstadoMantenimiento("ESTADO_MANTENIMIENTO");
        dto.setDiagnostico("Revision semestral");
        dto.setTrabajoRealizado("Limpieza y actualizacion");
        dto.setCosto(BigDecimal.ZERO);
        dto.setEstadoPosterior("OPERATIVO");
        dto.setObservaciones("Equipo operativo");
        dto.setFechaMantenimiento(LocalDateTime.now());

        when(computadoraRepository.findById(1L)).thenReturn(Optional.of(computadora));
        when(usuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(usuario));
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenAnswer(i -> {
            Mantenimiento m = i.getArgument(0);
            m.setId(101L);
            return m;
        });

        MantenimientoResponseDTO response = mantenimientoService.guardar(dto);

        assertNotNull(response);
        assertEquals("Responsable asignado", response.getNombreResponsable());
        assertEquals("OPERATIVO", computadora.getEstado());
        verify(mantenimientoRepository, times(1)).save(any(Mantenimiento.class));
    }
}
