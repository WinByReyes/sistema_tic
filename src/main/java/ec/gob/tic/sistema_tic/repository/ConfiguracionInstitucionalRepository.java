package ec.gob.tic.sistema_tic.repository;

import ec.gob.tic.sistema_tic.entity.ConfiguracionInstitucional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionInstitucionalRepository
        extends JpaRepository<
        ConfiguracionInstitucional,
        Long> {
}