package ec.gob.tic.sistema_tic.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MantenimientoResponseDTO {

    private Long id;


    // ==========================================
    // COMPUTADORA
    // ==========================================

    private Long computadoraId;

    private String serieComputadora;

    private String nombreEquipo;


    // ==========================================
    // USUARIO QUE REGISTRÓ
    // ==========================================

    private Long usuarioId;

    private String nombreUsuario;

    private String usuario;


    // ==========================================
    // RESPONSABLE
    // ==========================================

    private Long responsableId;

    private String nombreResponsable;

    private String cargoResponsable;


    // ==========================================
    // FECHAS
    // ==========================================

    private LocalDateTime fechaHora;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaMantenimiento;


    // ==========================================
    // MANTENIMIENTO
    // ==========================================

    private String tipoMantenimiento;

    private String estadoMantenimiento;

    private String diagnostico;

    private String trabajoRealizado;

    private BigDecimal costo;


    // ==========================================
    // ESTADO DE LA COMPUTADORA
    // ==========================================

    private String estadoAnterior;

    private String estadoPosterior;


    // ==========================================
    // OBSERVACIONES
    // ==========================================

    private String observaciones;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public MantenimientoResponseDTO() {
    }


    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getComputadoraId() {
        return computadoraId;
    }

    public void setComputadoraId(
            Long computadoraId) {

        this.computadoraId =
                computadoraId;
    }


    public String getSerieComputadora() {
        return serieComputadora;
    }

    public void setSerieComputadora(
            String serieComputadora) {

        this.serieComputadora =
                serieComputadora;
    }


    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(
            String nombreEquipo) {

        this.nombreEquipo =
                nombreEquipo;
    }


    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId =
                usuarioId;
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(
            String nombreUsuario) {

        this.nombreUsuario =
                nombreUsuario;
    }


    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario =
                usuario;
    }


    public Long getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(
            Long responsableId) {

        this.responsableId =
                responsableId;
    }


    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(
            String nombreResponsable) {

        this.nombreResponsable =
                nombreResponsable;
    }


    public String getCargoResponsable() {
        return cargoResponsable;
    }

    public void setCargoResponsable(
            String cargoResponsable) {

        this.cargoResponsable =
                cargoResponsable;
    }


    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(
            LocalDateTime fechaHora) {

        this.fechaHora =
                fechaHora;
    }


    public String getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(
            String tipoMantenimiento) {

        this.tipoMantenimiento =
                tipoMantenimiento;
    }


    public String getEstadoMantenimiento() {
        return estadoMantenimiento;
    }

    public void setEstadoMantenimiento(
            String estadoMantenimiento) {

        this.estadoMantenimiento =
                estadoMantenimiento;
    }


    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(
            String diagnostico) {

        this.diagnostico =
                diagnostico;
    }


    public String getTrabajoRealizado() {
        return trabajoRealizado;
    }

    public void setTrabajoRealizado(
            String trabajoRealizado) {

        this.trabajoRealizado =
                trabajoRealizado;
    }


    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo =
                costo;
    }


    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(
            String estadoAnterior) {

        this.estadoAnterior =
                estadoAnterior;
    }


    public String getEstadoPosterior() {
        return estadoPosterior;
    }

    public void setEstadoPosterior(
            String estadoPosterior) {

        this.estadoPosterior =
                estadoPosterior;
    }


    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(
            String observaciones) {

        this.observaciones =
                observaciones;
    }


    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(
            LocalDateTime fechaCreacion) {

        this.fechaCreacion =
                fechaCreacion;
    }


    public LocalDateTime getFechaMantenimiento() {
        return fechaMantenimiento;
    }

    public void setFechaMantenimiento(
            LocalDateTime fechaMantenimiento) {

        this.fechaMantenimiento =
                fechaMantenimiento;
    }
}