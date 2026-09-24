package ec.gob.tic.sistema_tic.dto;

import java.util.List;

public class ConsultaFuncionarioResponseDTO {
    private FuncionarioResponseDTO funcionario;

    private List<ComputadoraResponseDTO> computadoras;

    private List<MantenimientoResponseDTO> mantenimientos;

    private List<EquipoTecnologicoResponseDTO> equiposTecnologicos;

    public ConsultaFuncionarioResponseDTO
            (FuncionarioResponseDTO funcionario,
             List<ComputadoraResponseDTO> computadoras,
             List<MantenimientoResponseDTO> mantenimientos) {
        this.funcionario = funcionario;
        this.computadoras = computadoras;
        this.mantenimientos = mantenimientos;
    }

    public FuncionarioResponseDTO getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(FuncionarioResponseDTO funcionario) {
        this.funcionario = funcionario;
    }

    public List<ComputadoraResponseDTO> getComputadoras() {
        return computadoras;
    }

    public void setComputadoras(List<ComputadoraResponseDTO> computadoras) {
        this.computadoras = computadoras;
    }

    public List<MantenimientoResponseDTO> getMantenimientos() {
        return mantenimientos;
    }

    public void setMantenimientos(List<MantenimientoResponseDTO> mantenimientos) {
        this.mantenimientos = mantenimientos;
    }

    public List<EquipoTecnologicoResponseDTO> getEquiposTecnologicos() {
        return equiposTecnologicos;
    }

    public void setEquiposTecnologicos(List<EquipoTecnologicoResponseDTO> equiposTecnologicos) {
        this.equiposTecnologicos = equiposTecnologicos;
    }
}
