package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.FuncionarioRequestDTO;
import ec.gob.tic.sistema_tic.dto.FuncionarioResponseDTO;
import ec.gob.tic.sistema_tic.entity.Computadora;
import ec.gob.tic.sistema_tic.entity.Funcionario;
import ec.gob.tic.sistema_tic.repository.AsignacionComputadoraRepository;
import ec.gob.tic.sistema_tic.repository.ComputadoraRepository;
import ec.gob.tic.sistema_tic.repository.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private ComputadoraRepository computadoraRepository;

    @Mock
    private AsignacionComputadoraRepository asignacionRepository;

    @InjectMocks
    private FuncionarioService funcionarioService;

    @Test
    void testGuardarFuncionario() {
        FuncionarioRequestDTO dto = new FuncionarioRequestDTO();
        dto.setCedula("0102030405");
        dto.setNombrePila("juan carlos perez");
        dto.setUnidadAdministrativa("TIC");
        dto.setCargo("ANALISTA");
        dto.setEstado("ACTIVO");

        when(funcionarioRepository.save(any(Funcionario.class))).thenAnswer(i -> {
            Funcionario f = i.getArgument(0);
            f.setId(10L);
            return f;
        });

        FuncionarioResponseDTO response = funcionarioService.guardar(dto);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("0102030405", response.getCedula());
        assertEquals("Juan Carlos Perez", response.getNombrePila());
    }

    @Test
    void testEliminarDesactivacionLogicaYLiberarComputadoras() {
        Funcionario f = new Funcionario();
        f.setId(5L);
        f.setNombrePila("Carlos Lopez");
        f.setEstado("ACTIVO");

        Computadora comp = new Computadora();
        comp.setId(20L);
        comp.setFuncionario(f);

        when(funcionarioRepository.findById(5L)).thenReturn(Optional.of(f));
        when(computadoraRepository.findByFuncionarioId(5L)).thenReturn(List.of(comp));
        when(asignacionRepository.findByComputadoraIdAndFechaFinIsNull(20L)).thenReturn(Optional.empty());

        funcionarioService.eliminar(5L);

        assertEquals("INACTIVO", f.getEstado());
        assertNull(comp.getFuncionario());
        verify(computadoraRepository, times(1)).save(comp);
        verify(funcionarioRepository, times(1)).save(f);
        verify(funcionarioRepository, never()).delete(any());
    }
}
