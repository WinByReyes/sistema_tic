package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.MantenimientoRequestDTO;
import ec.gob.tic.sistema_tic.dto.MantenimientoResponseDTO;
import ec.gob.tic.sistema_tic.entity.Catalogo;
import ec.gob.tic.sistema_tic.entity.Computadora;
import ec.gob.tic.sistema_tic.entity.Mantenimiento;
import ec.gob.tic.sistema_tic.entity.Responsable;
import ec.gob.tic.sistema_tic.entity.Usuario;
import ec.gob.tic.sistema_tic.repository.CatalogoRepository;
import ec.gob.tic.sistema_tic.repository.ComputadoraRepository;
import ec.gob.tic.sistema_tic.repository.MantenimientoRepository;
import ec.gob.tic.sistema_tic.repository.ResponsableRepository;
import ec.gob.tic.sistema_tic.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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
@MockitoSettings(strictness = Strictness.LENIENT)
class MantenimientoServiceTest {

    @Mock
    private MantenimientoRepository mantenimientoRepository;

    @Mock
    private ComputadoraRepository computadoraRepository;

    @Mock
    private ResponsableRepository responsableRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CatalogoRepository catalogoRepository;

    @InjectMocks
    private MantenimientoService mantenimientoService;

    private Computadora computadora;
    private Usuario usuario;
    private Responsable responsable;
    private Catalogo catTipo;
    private Catalogo catEstado;

    @BeforeEach
    void setUp() {
        computadora = new Computadora();
        computadora.setId(1L);
        computadora.setSerie("SN123456");
        computadora.setNombreEquipo("PC-01");
        computadora.setEstado("BUENO");

        usuario = new Usuario();
        usuario.setId(10L);
        usuario.setUsuario("admin");
        usuario.setNombre("Admin TIC");

        responsable = new Responsable();
        responsable.setId(2L);
        responsable.setNombre("Tecnico Juan");
        responsable.setEstado(true);

        catTipo = new Catalogo();
        catTipo.setTipo("TIPO_MANTENIMIENTO");
        catTipo.setNombre("PREVENTIVO");
        catTipo.setActivo(true);

        catEstado = new Catalogo();
        catEstado.setTipo("ESTADO_MANTENIMIENTO");
        catEstado.setNombre("FINALIZADO");
        catEstado.setActivo(true);

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
    void testGuardarConResponsableManual() {
        MantenimientoRequestDTO dto = new MantenimientoRequestDTO();
        dto.setComputadoraId(1L);
        dto.setResponsableManual("soporte externo acme");
        dto.setTipoMantenimiento("PREVENTIVO");
        dto.setEstadoMantenimiento("FINALIZADO");
        dto.setDiagnostico("Revision semestral");
        dto.setTrabajoRealizado("Limpieza y actualizacion");
        dto.setCosto(BigDecimal.ZERO);
        dto.setEstadoAnterior("BUENO");
        dto.setEstadoPosterior("EXCELENTE");
        dto.setObservaciones("Equipo operativo");
        dto.setFechaMantenimiento(LocalDateTime.now());

        when(computadoraRepository.findById(1L)).thenReturn(Optional.of(computadora));
        when(usuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(usuario));
        when(catalogoRepository.findByTipoAndNombreIgnoreCase("TIPO_MANTENIMIENTO", "PREVENTIVO"))
                .thenReturn(Optional.of(catTipo));
        when(catalogoRepository.findByTipoAndNombreIgnoreCase("ESTADO_MANTENIMIENTO", "FINALIZADO"))
                .thenReturn(Optional.of(catEstado));
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenAnswer(i -> {
            Mantenimiento m = i.getArgument(0);
            m.setId(100L);
            return m;
        });

        MantenimientoResponseDTO response = mantenimientoService.guardar(dto);

        assertNotNull(response);
        assertNull(response.getResponsableId());
        assertEquals("Soporte Externo Acme", response.getNombreResponsable());
        assertEquals("EXCELENTE", computadora.getEstado());
        verify(mantenimientoRepository, times(1)).save(any(Mantenimiento.class));
    }

    @Test
    void testGuardarSinResponsableLanzaExcepcion() {
        MantenimientoRequestDTO dto = new MantenimientoRequestDTO();
        dto.setComputadoraId(1L);
        dto.setResponsableId(null);
        dto.setResponsableManual("");
        dto.setTipoMantenimiento("PREVENTIVO");
        dto.setEstadoMantenimiento("FINALIZADO");

        when(computadoraRepository.findById(1L)).thenReturn(Optional.of(computadora));
        when(usuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(usuario));

        assertThrows(IllegalArgumentException.class, () -> mantenimientoService.guardar(dto));
        verify(mantenimientoRepository, never()).save(any());
    }
}
