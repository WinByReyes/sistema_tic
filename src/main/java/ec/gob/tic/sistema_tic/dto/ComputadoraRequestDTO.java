package ec.gob.tic.sistema_tic.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ComputadoraRequestDTO {

    @Size(max = 10, message = "La cédula no puede superar los 10 caracteres")
    private String cedulaFuncionario;

    @NotBlank(message = "La serie es obligatoria")
    @Size(max = 50, message = "La serie no puede superar los 50 caracteres")
    private String serie;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(max = 50, message = "El nombre del equipo no puede superar los 50 caracteres")
    private String nombreEquipo;

    @NotBlank(message = "La procedencia es obligatoria")
    @Size(max = 100, message = "La procedencia no puede superar los 100 caracteres")
    private String procedencia;

    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 100, message = "El tipo no puede superar los 100 caracteres")
    private String tipo;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no puede superar los 50 caracteres")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 50, message = "El modelo no puede superar los 50 caracteres")
    private String modelo;

    @NotBlank(message = "El tipo de procesador es obligatorio")
    @Size(max = 100, message = "El tipo de procesador no puede superar los 100 caracteres")
    private String tipoProcesador;

    @NotBlank(message = "La generación del procesador es obligatoria")
    @Size(max = 50, message = "La generación no puede superar los 50 caracteres")
    private String generacionProcesador;

    @NotBlank(message = "La velocidad del procesador es obligatoria")
    @Size(max = 50, message = "La velocidad no puede superar los 50 caracteres")
    private String velocidadProcesador;

    @NotBlank(message = "La memoria RAM es obligatoria")
    @Size(max = 50, message = "La memoria RAM no puede superar los 50 caracteres")
    private String memoriaRAM;

    @NotBlank(message = "El tipo de disco es obligatorio")
    @Size(max = 50, message = "El tipo de disco no puede superar los 50 caracteres")
    private String tipoDisco;

    @NotNull(message = "La capacidad del disco es obligatoria")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "La capacidad del disco no puede ser negativa")
    private BigDecimal capacidadDiscoGB;

    @NotBlank(message = "El sistema operativo es obligatorio")
    @Size(max = 50, message = "El sistema operativo no puede superar los 50 caracteres")
    private String sistemaOperativo;

    @NotBlank(message = "Office es obligatorio")
    @Size(max = 50, message = "Office no puede superar los 50 caracteres")
    private String office;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 50, message = "La ubicación no puede superar los 50 caracteres")
    private String ubicacion;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 50, message = "El estado no puede superar los 50 caracteres")
    private String estado;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ComputadoraRequestDTO() {
    }

    public String getCedulaFuncionario() {
        return cedulaFuncionario;
    }

    public void setCedulaFuncionario(String cedulaFuncionario) {
        this.cedulaFuncionario = cedulaFuncionario;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(String procedencia) {
        this.procedencia = procedencia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public String getTipoProcesador() {
        return tipoProcesador;
    }

    public void setTipoProcesador(String tipoProcesador) {
        this.tipoProcesador = tipoProcesador;
    }

    public String getGeneracionProcesador() {
        return generacionProcesador;
    }

    public void setGeneracionProcesador(String generacionProcesador) {
        this.generacionProcesador = generacionProcesador;
    }

    public String getVelocidadProcesador() {
        return velocidadProcesador;
    }

    public void setVelocidadProcesador(String velocidadProcesador) {
        this.velocidadProcesador = velocidadProcesador;
    }

    public String getMemoriaRAM() {
        return memoriaRAM;
    }

    public void setMemoriaRAM(String memoriaRAM) {
        this.memoriaRAM = memoriaRAM;
    }

    public String getTipoDisco() {
        return tipoDisco;
    }

    public void setTipoDisco(String tipoDisco) {
        this.tipoDisco = tipoDisco;
    }

    public BigDecimal getCapacidadDiscoGB() {
        return capacidadDiscoGB;
    }

    public void setCapacidadDiscoGB(BigDecimal capacidadDiscoGB) {
        this.capacidadDiscoGB = capacidadDiscoGB;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
