package ec.gob.tic.sistema_tic.dto;

import ec.gob.tic.sistema_tic.entity.Computadora;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ComputadoraResponseDTO {

    private Long id;

    private Long funcionarioId;
    private String cedulaFuncionario;
    private String nombreFuncionario;
    private String unidadAdministrativaFuncionario;

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
    private String antivirus;
    private String observacionSoftware;

    private String ip;
    private String macLan;
    private String macWifi;
    private String nroPR;
    private String observacionRed;

    private String ubicacion;
    private String estado;

    private LocalDateTime fechaCreacion;

    public ComputadoraResponseDTO() {
    }

    public ComputadoraResponseDTO(Computadora computadora) {
        if (computadora != null) {
            this.id = computadora.getId();

            if (computadora.getFuncionario() != null) {
                this.funcionarioId = computadora.getFuncionario().getId();
                this.cedulaFuncionario = computadora.getFuncionario().getCedula();
                this.nombreFuncionario = computadora.getFuncionario().getNombreCompleto();
                this.unidadAdministrativaFuncionario = computadora.getFuncionario().getUnidadAdministrativa();
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
            this.antivirus = computadora.getAntivirus();
            this.observacionSoftware = computadora.getObservacionSoftware();

            this.ip = computadora.getIp();
            this.macLan = computadora.getMacLan();
            this.macWifi = computadora.getMacWifi();
            this.nroPR = computadora.getNroPR();
            this.observacionRed = computadora.getObservacionRed();

            this.ubicacion = computadora.getUbicacion();
            this.estado = computadora.getEstado();

            this.fechaCreacion = computadora.getFechaCreacion();
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

    public String getAntivirus() {
        return antivirus;
    }

    public void setAntivirus(String antivirus) {
        this.antivirus = antivirus;
    }

    public String getObservacionSoftware() {
        return observacionSoftware;
    }

    public void setObservacionSoftware(String observacionSoftware) {
        this.observacionSoftware = observacionSoftware;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMacLan() {
        return macLan;
    }

    public void setMacLan(String macLan) {
        this.macLan = macLan;
    }

    public String getMacWifi() {
        return macWifi;
    }

    public void setMacWifi(String macWifi) {
        this.macWifi = macWifi;
    }

    public String getNroPR() {
        return nroPR;
    }

    public void setNroPR(String nroPR) {
        this.nroPR = nroPR;
    }

    public String getObservacionRed() {
        return observacionRed;
    }

    public void setObservacionRed(String observacionRed) {
        this.observacionRed = observacionRed;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
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
