package com.glc.smartcar.integration.fipe.port;

import com.glc.smartcar.integration.fipe.dto.FipeNameAndCode;
import com.glc.smartcar.integration.fipe.dto.FipeVeiculoDTO;

import java.util.List;

public interface FipePort {
    List<FipeNameAndCode> buscarMarcas();
    List<FipeNameAndCode> buscarModelos(String brandId);
    List<FipeNameAndCode> buscarAnos(String brandId, String modelId);
    FipeVeiculoDTO obterPreco(String brandId, String modelId, String yearId);
}
