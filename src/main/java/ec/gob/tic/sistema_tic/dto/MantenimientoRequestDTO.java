package ec.gob.tic.sistema_tic.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MantenimientoRequestDTO {

    @NotNull(message = "El ID de la computadora es obligatorio")
    private Long computadoraId;

    @NotBlank(message = "El responsable del mantenimiento es obligatorio")
    private String responsable;

    @NotBlank(message = "El tipo de mantenimiento es obligatorio")
    private String tipoMantenimiento;

    @NotBlank(message = "El estado del mantenimiento es obligatorio")
    private String estadoMantenimiento;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    @NotBlank(message = "El trabajo realizado es obligatorio")
    private String trabajoRealizado;

    @NotNull(message = "El costo es obligatorio")
    @DecimalMin(
            value = "0.00",
            message = "El costo no puede ser negativo"
    )
    private BigDecimal costo;

    @NotBlank(message = "El estado posterior es obligatorio")
    private String estadoPosterior;

    @NotBlank(message = "Las observaciones son obligatorias")
    private String observaciones;

    @NotNull(message = "La fecha de mantenimiento es obligatoria")
    private LocalDateTime fechaMantenimiento;

    public MantenimientoRequestDTO() {
    }

    public Long getComputadoraId() {
        return computadoraId;
    }

    public void setComputadoraId(Long computadoraId) {
        this.computadoraId = computadoraId;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(String tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public String getEstadoMantenimiento() {
        return estadoMantenimiento;
    }

    public void setEstadoMantenimiento(String estadoMantenimiento) {
        this.estadoMantenimiento = estadoMantenimiento;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTrabajoRealizado() {
        return trabajoRealizado;
    }

    public void setTrabajoRealizado(String trabajoRealizado) {
        this.trabajoRealizado = trabajoRealizado;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public String getEstadoPosterior() {
        return estadoPosterior;
    }

    public void setEstadoPosterior(String estadoPosterior) {
        this.estadoPosterior = estadoPosterior;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaMantenimiento() {
        return fechaMantenimiento;
    }

    public void setFechaMantenimiento(LocalDateTime fechaMantenimiento) {
        this.fechaMantenimiento = fechaMantenimiento;
    }
}