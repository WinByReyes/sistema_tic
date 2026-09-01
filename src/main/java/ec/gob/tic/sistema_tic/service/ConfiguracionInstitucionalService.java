package ec.gob.tic.sistema_tic.service;

import ec.gob.tic.sistema_tic.dto.ConfiguracionInstitucionalDTO;
import ec.gob.tic.sistema_tic.entity.ConfiguracionInstitucional;
import ec.gob.tic.sistema_tic.repository.ConfiguracionInstitucionalRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfiguracionInstitucionalService {

    private static final Long ID_CONFIGURACION = 1L;


    private final ConfiguracionInstitucionalRepository repository;


    public ConfiguracionInstitucionalService(
            ConfiguracionInstitucionalRepository repository) {

        this.repository =
                repository;
    }


    @Transactional(readOnly = true)
    public ConfiguracionInstitucionalDTO obtener() {

        ConfiguracionInstitucional configuracion = repository
                .findById(ID_CONFIGURACION)
                .orElseGet(() -> {

                    ConfiguracionInstitucional nueva =
                            new ConfiguracionInstitucional();

                    nueva.setId(
                            ID_CONFIGURACION
                    );

                    nueva.setNombreInstitucion(
                            "Sistema TIC"
                    );

                    return repository.save(
                            nueva
                    );
                });

        return new ConfiguracionInstitucionalDTO(configuracion);
    }


    @Transactional
    public ConfiguracionInstitucionalDTO actualizar(
            ConfiguracionInstitucionalDTO datos) {

        ConfiguracionInstitucional configuracion = repository
                .findById(ID_CONFIGURACION)
                .orElseGet(() -> {
                    ConfiguracionInstitucional nueva = new ConfiguracionInstitucional();
                    nueva.setId(ID_CONFIGURACION);
                    return nueva;
                });


        configuracion.setNombreInstitucion(
                datos.getNombreInstitucion()
        );


        configuracion.setUnidadAdministrativa(
                datos.getUnidadAdministrativa()
        );


        configuracion.setDireccion(
                datos.getDireccion()
        );


        configuracion.setTelefono(
                datos.getTelefono()
        );


        configuracion.setCorreo(
                datos.getCorreo()
        );


        ConfiguracionInstitucional guardada = repository.save(
                configuracion
        );

        return new ConfiguracionInstitucionalDTO(guardada);
    }
}
