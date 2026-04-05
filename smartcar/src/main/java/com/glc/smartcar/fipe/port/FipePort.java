package com.glc.smartcar.fipe.port;

import com.glc.smartcar.fipe.dto.FipeNameAndCode;
import com.glc.smartcar.fipe.dto.FipeVeiculoDTO;

import java.util.List;

public interface FipePort {
    List<FipeNameAndCode> buscarMarcas();
    List<FipeNameAndCode> buscarModelos(String brandId);
    List<FipeNameAndCode> buscarAnos(String brandId, String modelId);
    FipeVeiculoDTO obterPreco(String brandId, String modelId, String yearId);
}
