package ec.gob.tic.sistema_tic.dto;

import ec.gob.tic.sistema_tic.entity.Computadora;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ComputadoraResponseDTO {

    private Long id;

    private Long funcionarioId;
    private String cedulaFuncionario;
    private String nombreFuncionario;

    private String serie;
    private String nombreEquipo;
    private String procedencia;
    private String tipo;
    private String marca;
    private String modelo;

    private String tipoProcesador;
    private String generacionProcesador;
    private String velocidadProcesador;
    private String memoriaRAM;

    private String tipoDisco;
    private BigDecimal capacidadDiscoGB;

    private String sistemaOperativo;
    private String office;
    private String ubicacion;
    private String estado;

    private LocalDateTime fechaCreacion;

    public ComputadoraResponseDTO() {
    }

    public ComputadoraResponseDTO(Computadora computadora) {

        this.id = computadora.getId();

        if (computadora.getFuncionario() != null) {
            this.funcionarioId = computadora.getFuncionario().getId();
            this.cedulaFuncionario = computadora.getFuncionario().getCedula();
            this.nombreFuncionario = computadora.getFuncionario().getNombrePila();
        }

        this.serie = computadora.getSerie();
        this.nombreEquipo = computadora.getNombreEquipo();
        this.procedencia = computadora.getProcedencia();
        this.tipo = computadora.getTipo();
        this.marca = computadora.getMarca();
        this.modelo = computadora.getModelo();

        this.tipoProcesador = computadora.getTipoProcesador();
        this.generacionProcesador = computadora.getGeneracionProcesador();
        this.velocidadProcesador = computadora.getVelocidadProcesador();
        this.memoriaRAM = computadora.getMemoriaRAM();

        this.tipoDisco = computadora.getTipoDisco();
        this.capacidadDiscoGB = computadora.getCapacidadDiscoGB();

        this.sistemaOperativo = computadora.getSistemaOperativo();
        this.office = computadora.getOffice();
        this.ubicacion = computadora.getUbicacion();
        this.estado = computadora.getEstado();

        this.fechaCreacion = computadora.getFechaCreacion();
    }

    public Long getId() {
        return id;
    }

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public String getCedulaFuncionario() {
        return cedulaFuncionario;
    }

    public String getNombreFuncionario() {
        return nombreFuncionario;
    }

    public String getSerie() {
        return serie;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public String getProcedencia() {
        return procedencia;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getTipoProcesador() {
        return tipoProcesador;
    }

    public String getGeneracionProcesador() {
        return generacionProcesador;
    }

    public String getVelocidadProcesador() {
        return velocidadProcesador;
    }

    public String getMemoriaRAM() {
        return memoriaRAM;
    }

    public String getTipoDisco() {
        return tipoDisco;
    }

    public BigDecimal getCapacidadDiscoGB() {
        return capacidadDiscoGB;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public String getOffice() {
        return office;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getEstado()
    {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
