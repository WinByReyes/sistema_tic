package ec.gob.tic.sistema_tic.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class EquipoTecnologicoRequestDTO {

    private Long funcionarioId;

    @Size(max = 10, message = "La cédula no puede superar los 10 caracteres")
    private String cedulaFuncionario;

    @NotBlank(message = "El tipo de equipo es obligatorio")
    @Size(max = 100, message = "El tipo de equipo no puede superar los 100 caracteres")
    private String tipoEquipo;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no puede superar los 50 caracteres")
    private String marca;

    @Size(max = 100, message = "El modelo no puede superar los 100 caracteres")
    private String modelo;

    @Size(max = 100, message = "La serie no puede superar los 100 caracteres")
    private String serie;

    private String detalle;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 50, message = "El estado no puede superar los 50 caracteres")
    private String estado;

    @Size(max = 100, message = "La etiqueta de constatación no puede superar los 100 caracteres")
    private String etiquetaConstatacion;

    @Size(max = 50, message = "El Nro. P.R. no puede superar los 50 caracteres")
    private String nroPR;

    @Size(max = 100, message = "La IP / teléfono no puede superar los 100 caracteres")
    private String ipTelefono;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precio;

    public EquipoTecnologicoRequestDTO() {
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
}