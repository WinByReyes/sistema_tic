package ec.gob.tic.sistema_tic.dto;

import ec.gob.tic.sistema_tic.entity.Mantenimiento;

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
    private String tipoEquipo;
    private String marcaEquipo;
    private String modeloEquipo;
    private String ubicacionEquipo;
    private String estadoActualComputadora;

    // ==========================================
    // FUNCIONARIO
    // ==========================================

    private Long funcionarioId;
    private String cedulaFuncionario;
    private String nombreFuncionario;

    // ==========================================
    // USUARIO QUE REGISTRÓ
    // ==========================================

    private Long usuarioId;
    private String nombreUsuario;
    private String usuario;

    // ==========================================
    // RESPONSABLE
    // ==========================================

    private String nombreResponsable;

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

    public MantenimientoResponseDTO(Mantenimiento m) {
        if (m != null) {
            this.id = m.getId();

            if (m.getComputadora() != null) {
                this.computadoraId = m.getComputadora().getId();
                this.serieComputadora = m.getComputadora().getSerie();
                this.nombreEquipo = m.getComputadora().getNombreEquipo();
                this.tipoEquipo = m.getComputadora().getTipo();
                this.marcaEquipo = m.getComputadora().getMarca();
                this.modeloEquipo = m.getComputadora().getModelo();
                this.ubicacionEquipo = m.getComputadora().getUbicacion();
                this.estadoActualComputadora = m.getComputadora().getEstado();

                if (m.getComputadora().getFuncionario() != null) {
                    this.funcionarioId = m.getComputadora().getFuncionario().getId();
                    this.cedulaFuncionario = m.getComputadora().getFuncionario().getCedula();
                    this.nombreFuncionario = m.getComputadora().getFuncionario().getNombreCompleto();
                }
            }

            if (m.getUsuario() != null) {
                this.usuarioId = m.getUsuario().getId();
                this.nombreUsuario = m.getUsuario().getNombre();
                this.usuario = m.getUsuario().getUsuario();
            }

            this.nombreResponsable = m.getResponsable();

            this.fechaHora = m.getFechaHora();
            this.fechaCreacion = m.getFechaCreacion();
            this.fechaMantenimiento = m.getFechaMantenimiento();
            this.tipoMantenimiento = m.getTipoMantenimiento();
            this.estadoMantenimiento = m.getEstadoMantenimiento();
            this.diagnostico = m.getDiagnostico();
            this.trabajoRealizado = m.getTrabajoRealizado();
            this.costo = m.getCosto();
            this.estadoPosterior = m.getEstadoPosterior();
            this.observaciones = m.getObservaciones();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getComputadoraId() {
        return computadoraId;
    }

    public void setComputadoraId(Long computadoraId) {
        this.computadoraId = computadoraId;
    }

    public String getSerieComputadora() {
        return serieComputadora;
    }

    public void setSerieComputadora(String serieComputadora) {
        this.serieComputadora = serieComputadora;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getMarcaEquipo() {
        return marcaEquipo;
    }

    public void setMarcaEquipo(String marcaEquipo) {
        this.marcaEquipo = marcaEquipo;
    }

    public String getModeloEquipo() {
        return modeloEquipo;
    }

    public void setModeloEquipo(String modeloEquipo) {
        this.modeloEquipo = modeloEquipo;
    }

    public String getUbicacionEquipo() {
        return ubicacionEquipo;
    }

    public void setUbicacionEquipo(String ubicacionEquipo) {
        this.ubicacionEquipo = ubicacionEquipo;
    }

    public String getEstadoActualComputadora() {
        return estadoActualComputadora;
    }

    public void setEstadoActualComputadora(String estadoActualComputadora) {
        this.estadoActualComputadora = estadoActualComputadora;
    }

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public String getCedulaFuncionario() {
        return cedulaFuncionario;
    }

    public void setCedulaFuncionario(String cedulaFuncionario) {
        this.cedulaFuncionario = cedulaFuncionario;
    }

    public String getNombreFuncionario() {
        return nombreFuncionario;
    }

    public void setNombreFuncionario(String nombreFuncionario) {
        this.nombreFuncionario = nombreFuncionario;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaMantenimiento() {
        return fechaMantenimiento;
    }

    public void setFechaMantenimiento(LocalDateTime fechaMantenimiento) {
        this.fechaMantenimiento = fechaMantenimiento;
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
}
