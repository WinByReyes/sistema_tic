package ec.gob.tic.sistema_tic.dto;

import java.util.List;

/**
 * Resumen general del inventario almacenado en el sistema.
 *
 * <p>Combina el equipamiento tecnológico y las computadoras con las
 * categorías (tipos) registrados en la base de datos.</p>
 */
public class InventarioResumenDTO {

    private Long totalComputadoras;
    private Long totalEquipamiento;
    private Long totalGeneral;

    private List<ResumenEquipamientoDTO> computadorasPorTipo;
    private List<ResumenEquipamientoDTO> equipamientoPorTipo;

    private Integer anio;
    private String desde;
    private String hasta;

    public InventarioResumenDTO() {
    }

    public Long getTotalComputadoras() {
        return totalComputadoras;
    }

    public void setTotalComputadoras(Long totalComputadoras) {
        this.totalComputadoras = totalComputadoras;
    }

    public Long getTotalEquipamiento() {
        return totalEquipamiento;
    }

    public void setTotalEquipamiento(Long totalEquipamiento) {
        this.totalEquipamiento = totalEquipamiento;
    }

    public Long getTotalGeneral() {
        return totalGeneral;
    }

    public void setTotalGeneral(Long totalGeneral) {
        this.totalGeneral = totalGeneral;
    }

    public List<ResumenEquipamientoDTO> getComputadorasPorTipo() {
        return computadorasPorTipo;
    }

    public void setComputadorasPorTipo(List<ResumenEquipamientoDTO> computadorasPorTipo) {
        this.computadorasPorTipo = computadorasPorTipo;
    }

    public List<ResumenEquipamientoDTO> getEquipamientoPorTipo() {
        return equipamientoPorTipo;
    }

    public void setEquipamientoPorTipo(List<ResumenEquipamientoDTO> equipamientoPorTipo) {
        this.equipamientoPorTipo = equipamientoPorTipo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public String getHasta() {
        return hasta;
    }

    public void setHasta(String hasta) {
        this.hasta = hasta;
    }
}