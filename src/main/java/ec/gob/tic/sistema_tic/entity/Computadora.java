package ec.gob.tic.sistema_tic.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "computadores")
public class Computadora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "funcionario_id",
            nullable = true
    )
    private Funcionario funcionario;

    @NotBlank(message = "La serie es obligatoria")
    @Size(max = 50, message = "La serie no puede superar los 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String serie;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(max = 50, message = "El nombre del equipo no puede superar los 50 caracteres")
    @Column(name = "nombre_equipo", nullable = false, length = 50)
    private String nombreEquipo;

    @Size(max = 100, message = "La procedencia no puede superar los 100 caracteres")
    @Column(nullable = true, length = 100)
    private String procedencia = "Institucional";

    @NotBlank(message = "El tipo de computadora es obligatorio")
    @Size(max = 100, message = "El tipo no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String tipo;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no puede superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 50, message = "El modelo no puede superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String modelo;

    @NotBlank(message = "El tipo de procesador es obligatorio")
    @Size(max = 100, message = "El tipo de procesador no puede superar los 100 caracteres")
    @Column(name = "tipo_procesador", nullable = false, length = 100)
    private String tipoProcesador;

    @NotBlank(message = "La generación del procesador es obligatoria")
    @Size(max = 50, message = "La generación no puede superar los 50 caracteres")
    @Column(name = "generacion_procesador", nullable = false, length = 50)
    private String generacionProcesador;

    @Size(max = 50, message = "La velocidad no puede superar los 50 caracteres")
    @Column(name = "velocidad_procesador", nullable = true, length = 50)
    private String velocidadProcesador = "N/A";

    @NotBlank(message = "La memoria RAM es obligatoria")
    @Size(max = 50, message = "La memoria RAM no puede superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String memoriaRAM;

    @NotBlank(message = "El tipo de disco es obligatorio")
    @Size(max = 50, message = "El tipo de disco no puede superar los 50 caracteres")
    @Column(name = "tipo_disco", nullable = false, length = 50)
    private String tipoDisco;

    @NotNull(message = "La capacidad del disco es obligatoria")
    @DecimalMin(value = "0.0", inclusive = false, message = "La capacidad del disco debe ser mayor a 0")
    @Column(name = "capacidad_disco_gb", nullable = false)
    private BigDecimal capacidadDiscoGB;

    @NotBlank(message = "El sistema operativo es obligatorio")
    @Size(max = 50, message = "El sistema operativo no puede superar los 50 caracteres")
    @Column(name = "sistema_operativo", nullable = false, length = 50)
    private String sistemaOperativo;

    @Size(max = 50, message = "Office no puede superar los 50 caracteres")
    @Column(nullable = true, length = 50)
    private String office;

    @Size(max = 50, message = "El antivirus no puede superar los 50 caracteres")
    @Column(nullable = true, length = 50)
    private String antivirus;

    @Column(name = "observacion_software", columnDefinition = "TEXT")
    private String observacionSoftware;

    @Size(max = 50, message = "La IP no puede superar los 50 caracteres")
    @Column(nullable = true, length = 50)
    private String ip;

    @Size(max = 50, message = "La MAC LAN no puede superar los 50 caracteres")
    @Column(name = "mac_lan", nullable = true, length = 50)
    private String macLan;

    @Size(max = 50, message = "La MAC WIFI no puede superar los 50 caracteres")
    @Column(name = "mac_wifi", nullable = true, length = 50)
    private String macWifi;

    @Size(max = 50, message = "El Nro. P.R. no puede superar los 50 caracteres")
    @Column(name = "nro_pr", nullable = true, length = 50)
    private String nroPR;

    @Column(name = "observacion_red", columnDefinition = "TEXT")
    private String observacionRed;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 50, message = "La ubicación no puede superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String ubicacion;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 50, message = "El estado no puede superar los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Computadora() {
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

    public String getOfimatica() {
        return office;
    }

    public void setOfimatica(String ofimatica) {
        this.office = ofimatica;
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
