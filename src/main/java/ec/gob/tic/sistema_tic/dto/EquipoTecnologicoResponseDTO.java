package ec.gob.tic.sistema_tic.dto;

import ec.gob.tic.sistema_tic.entity.EquipoTecnologico;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EquipoTecnologicoResponseDTO {

    private Long id;

    private Long funcionarioId;
    private String cedulaFuncionario;
    private String nombreFuncionario;
    private String unidadAdministrativaFuncionario;

    private String tipoEquipo;
    private String marca;
    private String modelo;
    private String serie;
    private String detalle;
    private String estado;
    private String etiquetaConstatacion;
    private String nroPR;
    private String ipTelefono;
    private BigDecimal precio;

    private LocalDateTime fechaCreacion;

    public EquipoTecnologicoResponseDTO() {
    }

    public EquipoTecnologicoResponseDTO(EquipoTecnologico equipo) {
        if (equipo != null) {
            this.id = equipo.getId();

            if (equipo.getFuncionario() != null) {
                this.funcionarioId = equipo.getFuncionario().getId();
                this.cedulaFuncionario = equipo.getFuncionario().getCedula();
                this.nombreFuncionario = equipo.getFuncionario().getNombreCompleto();
                this.unidadAdministrativaFuncionario = equipo.getFuncionario().getUnidadAdministrativa();
            }

            this.tipoEquipo = equipo.getTipoEquipo();
            this.marca = equipo.getMarca();
            this.modelo = equipo.getModelo();
            this.serie = equipo.getSerie();
            this.detalle = equipo.getDetalle();
            this.estado = equipo.getEstado();
            this.etiquetaConstatacion = equipo.getEtiquetaConstatacion();
            this.nroPR = equipo.getNroPR();
            this.ipTelefono = equipo.getIpTelefono();
            this.precio = equipo.getPrecio();

            this.fechaCreacion = equipo.getFechaCreacion();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUnidadAdministrativaFuncionario() {
        return unidadAdministrativaFuncionario;
    }

    public void setUnidadAdministrativaFuncionario(String unidadAdministrativaFuncionario) {
        this.unidadAdministrativaFuncionario = unidadAdministrativaFuncionario;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEtiquetaConstatacion() {
        return etiquetaConstatacion;
    }

    public void setEtiquetaConstatacion(String etiquetaConstatacion) {
        this.etiquetaConstatacion = etiquetaConstatacion;
    }

    public String getNroPR() {
        return nroPR;
    }

    public void setNroPR(String nroPR) {
        this.nroPR = nroPR;
    }

    public String getIpTelefono() {
        return ipTelefono;
    }

    public void setIpTelefono(String ipTelefono) {
        this.ipTelefono = ipTelefono;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}