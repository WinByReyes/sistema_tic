package ec.gob.tic.sistema_tic.dto;

import ec.gob.tic.sistema_tic.entity.Responsable;

import java.time.LocalDateTime;

public class ResponsableResponseDTO {

    private Long id;
    private String nombre;
    private String cargo;
    private Boolean estado;
    private LocalDateTime fechaCreacion;

    public ResponsableResponseDTO() {
    }

    public ResponsableResponseDTO(Responsable responsable) {
        if (responsable != null) {
            this.id = responsable.getId();
            this.nombre = responsable.getNombre();
            this.cargo = responsable.getCargo();
            this.estado = responsable.getEstado();
            this.fechaCreacion = responsable.getFechaCreacion();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
