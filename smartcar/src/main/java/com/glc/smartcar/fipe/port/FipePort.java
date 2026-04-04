package com.glc.smartcar.fipe.port;

import com.glc.smartcar.fipe.dto.FipeMarcaDTO;
import com.glc.smartcar.fipe.dto.FipeModelosResponse;
import com.glc.smartcar.fipe.dto.FipeVeiculoDTO;

import java.util.List;

public interface FipePort {
    List<FipeMarcaDTO> buscarMarcas();
    FipeModelosResponse buscarModelos(String brandId);
    List<FipeMarcaDTO> buscarAnos(String brandId, String modelId);
    FipeVeiculoDTO obterPreco(String brandId, String modelId, String yearId);
}
