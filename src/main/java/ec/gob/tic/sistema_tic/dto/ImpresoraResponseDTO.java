package ec.gob.tic.sistema_tic.dto;

import ec.gob.tic.sistema_tic.entity.Impresora;

import java.time.LocalDateTime;

public class ImpresoraResponseDTO {

    private Long id;

    private Long funcionarioId;
    private String cedulaFuncionario;
    private String nombreFuncionario;
    private String unidadAdministrativaFuncionario;

    private String tipoEquipo;
    private String marca;
    private String modelo;
    private String serie;
    private String estado;

    private LocalDateTime fechaCreacion;

    public ImpresoraResponseDTO() {
    }

    public ImpresoraResponseDTO(Impresora impresora) {
        if (impresora != null) {
            this.id = impresora.getId();

            if (impresora.getFuncionario() != null) {
                this.funcionarioId = impresora.getFuncionario().getId();
                this.cedulaFuncionario = impresora.getFuncionario().getCedula();
                this.nombreFuncionario = impresora.getFuncionario().getNombreCompleto();
                this.unidadAdministrativaFuncionario = impresora.getFuncionario().getUnidadAdministrativa();
            }

            this.tipoEquipo = impresora.getTipoEquipo();
            this.marca = impresora.getMarca();
            this.modelo = impresora.getModelo();
            this.serie = impresora.getSerie();
            this.estado = impresora.getEstado();

            this.fechaCreacion = impresora.getFechaCreacion();
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