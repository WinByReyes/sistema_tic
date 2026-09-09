package ec.gob.tic.sistema_tic.dto;

import ec.gob.tic.sistema_tic.entity.Funcionario;

import java.time.LocalDateTime;

public class FuncionarioResponseDTO {

    private Long id;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String nombrePila;
    private String unidadAdministrativa;
    private String cargo;
    private String estado;
    private String codigoBiometrico;
    private LocalDateTime fechaCreacion;

    public FuncionarioResponseDTO() {
    }

    public FuncionarioResponseDTO(Funcionario funcionario) {
        if (funcionario != null) {
            this.id = funcionario.getId();
            this.cedula = funcionario.getCedula();
            this.nombres = funcionario.getNombres();
            this.apellidos = funcionario.getApellidos();
            this.nombreCompleto = funcionario.getNombreCompleto();
            this.nombrePila = funcionario.getNombrePila();
            this.unidadAdministrativa = funcionario.getUnidadAdministrativa();
            this.cargo = funcionario.getCargo();
            this.estado = funcionario.getEstado();
            this.codigoBiometrico = funcionario.getCodigoBiometrico();
            this.fechaCreacion = funcionario.getFechaCreacion();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
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
