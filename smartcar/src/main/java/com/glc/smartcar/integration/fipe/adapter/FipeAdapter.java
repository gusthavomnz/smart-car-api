package com.glc.smartcar.integration.fipe.adapter;

import com.glc.smartcar.integration.fipe.dto.FipeNameAndCode;
import com.glc.smartcar.integration.fipe.dto.FipeVeiculoDTO;
import com.glc.smartcar.integration.fipe.port.FipePort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FipeAdapter implements FipePort {

    private final FipeClient fipeClient;

    public FipeAdapter(FipeClient fipeClient) {
        this.fipeClient = fipeClient;
    }

    @Override
    public List<FipeNameAndCode> buscarMarcas() {
        return fipeClient.buscarMarcas();
    }

    @Override
    public List<FipeNameAndCode> buscarModelos(String brandId) {
        return fipeClient.buscarModelos(brandId);
    }

    @Override
    public List<FipeNameAndCode> buscarAnos(String brandId, String modelId) {
        return fipeClient.buscarAnos(brandId, modelId);
    }

    @Override
    public FipeVeiculoDTO obterPreco(String brandId, String modelId, String yearId) {
        return fipeClient.obterPreco(brandId, modelId, yearId);
    }
}
