package ec.gob.tic.sistema_tic.dto;


import java.util.List;


public class ConsultaComputadoraResponseDTO {


    private ComputadoraResponseDTO
            computadora;


    private FuncionarioResponseDTO
            funcionarioActual;


    private List<AsignacionComputadoraResponseDTO>
            historialAsignaciones;


    private List<MantenimientoResponseDTO>
            historialMantenimientos;


    public ConsultaComputadoraResponseDTO(

            ComputadoraResponseDTO computadora,

            FuncionarioResponseDTO funcionarioActual,

            List<AsignacionComputadoraResponseDTO>
                    historialAsignaciones,

            List<MantenimientoResponseDTO>
                    historialMantenimientos) {


        this.computadora =
                computadora;


        this.funcionarioActual =
                funcionarioActual;


        this.historialAsignaciones =
                historialAsignaciones;


        this.historialMantenimientos =
                historialMantenimientos;

    }


    public ComputadoraResponseDTO
    getComputadora() {

        return computadora;

    }


    public FuncionarioResponseDTO
    getFuncionarioActual() {

        return funcionarioActual;

    }


    public List<AsignacionComputadoraResponseDTO>
    getHistorialAsignaciones() {

        return historialAsignaciones;

    }


    public List<MantenimientoResponseDTO>
    getHistorialMantenimientos() {

        return historialMantenimientos;

    }

}