package ec.gob.tic.sistema_tic.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipos_tecnologicos")
public class EquipoTecnologico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "funcionario_id",
            nullable = true
    )
    private Funcionario funcionario;

    @NotBlank(message = "El tipo de equipo es obligatorio")
    @Size(max = 100, message = "El tipo de equipo no puede superar los 100 caracteres")
    @Column(name = "tipo_equipo", nullable = false, length = 100)
    private String tipoEquipo;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no puede superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String marca;

    @Size(max = 100, message = "El modelo no puede superar los 100 caracteres")
    @Column(nullable = true, length = 100)
    private String modelo;

    @Size(max = 100, message = "La serie no puede superar los 100 caracteres")
    @Column(nullable = true, length = 100)
    private String serie;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String detalle;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 50, message = "El estado no puede superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String estado;

    @Size(max = 100, message = "La etiqueta de constatación no puede superar los 100 caracteres")
    @Column(name = "etiqueta_constatacion", nullable = true, length = 100)
    private String etiquetaConstatacion;

    @Size(max = 50, message = "El Nro. P.R. no puede superar los 50 caracteres")
    @Column(name = "nro_pr", nullable = true, length = 50)
    private String nroPR;

    @Size(max = 100, message = "La IP / teléfono no puede superar los 100 caracteres")
    @Column(name = "ip_telefono", nullable = true, length = 100)
    private String ipTelefono;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @Column(nullable = true, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public EquipoTecnologico() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
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