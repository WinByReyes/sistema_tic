package ec.gob.tic.sistema_tic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Módulo independiente de Impresoras.
 *
 * <p>Modelo independiente de {@link EquipoTecnologico}: su propio módulo,
 * su propia tabla y sus propios endpoints, reutilizando los catálogos
 * existentes (TIPO_EQUIPO_TECNOLOGICO, MARCA_EQUIPO_TECNOLOGICO y
 * ESTADO_COMPUTADORA).</p>
 */
@Entity
@Table(name = "impresoras")
public class Impresora {

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

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 50, message = "El estado no puede superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Impresora() {
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}