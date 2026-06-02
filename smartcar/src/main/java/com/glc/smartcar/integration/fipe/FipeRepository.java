package com.glc.smartcar.integration.fipe;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FipeRepository extends JpaRepository<Fipe,Long> {

    Optional<Fipe> findByCodigoMarcaAndCodigoModeloAndCodigoAno(
            String codigoMarca,
            String codigoModelo,
            String codigoAno
    );


}
