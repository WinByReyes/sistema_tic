package ec.gob.tic.sistema_tic.dto;


import ec.gob.tic.sistema_tic.entity.AsignacionComputadora;

import java.time.LocalDateTime;


public class AsignacionComputadoraResponseDTO {


    private Long id;

    private Long funcionarioId;

    private String nombreFuncionario;

    private String cedulaFuncionario;

    private LocalDateTime fechaAsignacion;

    private LocalDateTime fechaFin;

    private String observaciones;


    public AsignacionComputadoraResponseDTO() {

    }


    public AsignacionComputadoraResponseDTO(
            AsignacionComputadora asignacion) {


        this.id =
                asignacion.getId();


        if (
                asignacion.getFuncionario() != null
        ) {

            this.funcionarioId =
                    asignacion
                            .getFuncionario()
                            .getId();


            this.nombreFuncionario =
                    asignacion
                            .getFuncionario()
                            .getNombrePila();


            this.cedulaFuncionario =
                    asignacion
                            .getFuncionario()
                            .getCedula();

        }


        this.fechaAsignacion =
                asignacion.getFechaAsignacion();


        this.fechaFin =
                asignacion.getFechaFin();


        this.observaciones =
                asignacion.getObservaciones();

    }


    public Long getId() {

        return id;

    }


    public Long getFuncionarioId() {

        return funcionarioId;

    }


    public String getNombreFuncionario() {

        return nombreFuncionario;

    }


    public String getCedulaFuncionario() {

        return cedulaFuncionario;

    }


    public LocalDateTime getFechaAsignacion() {

        return fechaAsignacion;

    }


    public LocalDateTime getFechaFin() {

        return fechaFin;

    }


    public String getObservaciones() {

        return observaciones;

    }

}