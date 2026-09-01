package ec.gob.tic.sistema_tic.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "funcionarios")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La cédula es obligatoria")
    @Pattern(regexp = "\\d{10}", message = "La cédula debe contener exactamente 10 numeros ")
    @Column(nullable = false, unique = true, length = 10)
    private String cedula;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Column(name = "nombre_pila", nullable = false, length = 150)
    private String nombrePila;

    @NotBlank(message = "La unidad administrativa es obligatoria")
    @Size(max = 150, message = "La unidad administrativa no puede superar los 150 caracteres")
    @Column(name = "unidad_administrativa", nullable = false, length = 150)
    private String unidadAdministrativa;

    @NotBlank(message = "El cargo es obligatorio")
    @Size(max = 150, message = "El cargo no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String cargo;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 100, message = "El estado no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String estado;

    @NotBlank(message = "El código biométrico es obligatorio")
    @Size(max = 10, message = "El código biométrico no puede superar los 10 caracteres")
    @Column(name = "codigo_biometrico", nullable = false, unique = true, length = 10)
    private String codigoBiometrico;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Funcionario()
    {
        this.fechaCreacion = LocalDateTime.now();
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombrePila() {
        return nombrePila;
    }

    public void setNombrePila(String nombrePila) {
        this.nombrePila = nombrePila;
    }

    public String getUnidadAdministrativa() {
        return unidadAdministrativa;
    }

    public void setUnidadAdministrativa(String unidadAdministrativa) {
        this.unidadAdministrativa = unidadAdministrativa;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigoBiometrico() {
        return codigoBiometrico;
    }

    public void setCodigoBiometrico(String codigoBiometrico) {
        this.codigoBiometrico = codigoBiometrico;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

