package ec.gob.tic.sistema_tic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mantenimiento")
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ==========================================
    // COMPUTADORA
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "computadora_id",
            nullable = false
    )
    @NotNull(message = "La computadora es obligatoria")
    private Computadora computadora;


    // ==========================================
    // USUARIO QUE REGISTRA
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "usuario_id",
            nullable = false
    )
    @NotNull(message = "El usuario que registra es obligatorio")
    private Usuario usuario;


    // ==========================================
    // RESPONSABLE DEL MANTENIMIENTO
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "responsable_id",
            nullable = true
    )
    private Responsable responsable;


    public String getResponsableManual() {
        return responsableManual;
    }

    public void setResponsableManual(String responsableManual) {
        this.responsableManual = responsableManual;
    }

    @Column(
            name = "responsable_manual",
            length = 150
    )
    private String responsableManual;


    // ==========================================
    // FECHAS
    // ==========================================

    @Column(
            name = "fecha_hora",
            nullable = false
    )
    @NotNull
    private LocalDateTime fechaHora;


    // ==========================================
    // TIPO DE MANTENIMIENTO
    // ==========================================

    @Column(
            name = "tipo_mantenimiento",
            nullable = false,
            length = 100
    )
    @NotBlank(
            message = "El tipo de mantenimiento es obligatorio"
    )
    private String tipoMantenimiento;


    // ==========================================
    // ESTADO DEL MANTENIMIENTO
    // ==========================================

    @Column(
            name = "estado_mantenimiento",
            nullable = false,
            length = 100
    )
    @NotBlank(
            message = "El estado del mantenimiento es obligatorio"
    )
    private String estadoMantenimiento;


    // ==========================================
    // INFORMACIÓN
    // ==========================================

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    @NotBlank(
            message = "El diagnóstico es obligatorio"
    )
    private String diagnostico;


    @Column(
            name = "trabajo_realizado",
            nullable = false,
            columnDefinition = "TEXT"
    )
    @NotBlank(
            message = "El trabajo realizado es obligatorio"
    )
    private String trabajoRealizado;


    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    @NotNull(
            message = "El costo es obligatorio"
    )
    @DecimalMin(
            value = "0.0",
            message = "El costo no puede ser negativo"
    )
    private BigDecimal costo;


    // ==========================================
    // ESTADO DE LA COMPUTADORA ANTES
    // ==========================================

    @Column(
            name = "estado_anterior",
            nullable = false,
            length = 50
    )
    @NotBlank
    private String estadoAnterior;


    // ==========================================
    // ESTADO DE LA COMPUTADORA DESPUÉS
    // ==========================================

    @Column(
            name = "estado_posterior",
            nullable = false,
            length = 50
    )
    @NotBlank
    private String estadoPosterior;


    // ==========================================
    // OBSERVACIONES
    // ==========================================

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    @NotBlank
    private String observaciones;


    // ==========================================
    // FECHA DE CREACIÓN
    // ==========================================

    @Column(
            name = "fecha_creacion",
            nullable = false
    )
    @NotNull
    private LocalDateTime fechaCreacion;


    // ==========================================
    // FECHA DEL MANTENIMIENTO
    // ==========================================

    @Column(
            name = "fecha_mantenimiento",
            nullable = false
    )
    @NotNull
    private LocalDateTime fechaMantenimiento;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public Mantenimiento() {

        this.fechaHora =
                LocalDateTime.now();

        this.fechaCreacion =
                LocalDateTime.now();
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


    public Computadora getComputadora() {
        return computadora;
    }

    public void setComputadora(
            Computadora computadora) {

        this.computadora =
                computadora;
    }


    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(
            Usuario usuario) {

        this.usuario =
                usuario;
    }


    public Responsable getResponsable() {
        return responsable;
    }

    public void setResponsable(
            Responsable responsable) {

        this.responsable =
                responsable;
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

    public void setCosto(
            BigDecimal costo) {

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