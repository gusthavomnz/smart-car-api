package com.glc.smartcar.fipe.adapter;

import com.glc.smartcar.fipe.dto.FipeMarcaDTO;
import com.glc.smartcar.fipe.dto.FipeModelosResponse;
import com.glc.smartcar.fipe.dto.FipeVeiculoDTO;
import com.glc.smartcar.fipe.port.FipePort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FipeAdapter implements FipePort {

    private final FipeClient fipeClient;

    public FipeAdapter(FipeClient fipeClient) {
        this.fipeClient = fipeClient;
    }

    @Override
    public List<FipeMarcaDTO> buscarMarcas() {
        return fipeClient.buscarMarcas();
    }

    @Override
    public List<FipeModelosResponse> buscarModelos(String brandId) {
        return fipeClient.buscarModelos(brandId);
    }

    @Override
    public List<FipeMarcaDTO> buscarAnos(String brandId, String modelId) {
        return fipeClient.buscarAnos(brandId, modelId);
    }

    @Override
    public FipeVeiculoDTO obterPreco(String brandId, String modelId, String yearId) {
        return fipeClient.obterPreco(brandId, modelId, yearId);
    }
}
