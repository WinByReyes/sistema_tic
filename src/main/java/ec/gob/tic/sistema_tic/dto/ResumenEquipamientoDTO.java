package ec.gob.tic.sistema_tic.dto;

public class ResumenEquipamientoDTO {

    private String componente;
    private Long cantidad;

    public ResumenEquipamientoDTO() {
    }

    public ResumenEquipamientoDTO(String componente, Long cantidad) {
        this.componente = componente;
        this.cantidad = cantidad;
    }

    public String getComponente() {
        return componente;
    }

    public void setComponente(String componente) {
        this.componente = componente;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}