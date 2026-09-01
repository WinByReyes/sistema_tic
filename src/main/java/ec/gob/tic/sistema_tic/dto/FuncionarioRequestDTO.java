package ec.gob.tic.sistema_tic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FuncionarioRequestDTO {

    @NotBlank(message = "La cédula es obligatoria")
    @Size(max = 10, message = "La cédula no puede superar los 10 caracteres")
    private String cedula;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombrePila;

    @NotBlank(message = "La unidad administrativa es obligatoria")
    @Size(max = 150, message = "La unidad administrativa no puede superar los 150 caracteres")
    private String unidadAdministrativa;

    @NotBlank(message = "El cargo es obligatorio")
    @Size(max = 150, message = "El cargo no puede superar los 150 caracteres")
    private String cargo;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 100, message = "El estado no puede superar los 100 caracteres")
    private String estado;

    @NotBlank(message = "El código biométrico es obligatorio")
    @Size(max = 10, message = "El código biométrico no puede superar los 10 caracteres")
    private String codigoBiometrico;

    public FuncionarioRequestDTO() {
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
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
}