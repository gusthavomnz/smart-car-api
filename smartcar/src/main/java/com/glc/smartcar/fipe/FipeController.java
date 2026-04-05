package com.glc.smartcar.fipe;

import com.glc.smartcar.fipe.dto.FipeNameAndCode;
import com.glc.smartcar.fipe.dto.FipeVeiculoDTO;
import com.glc.smartcar.fipe.port.FipePort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fipe")
public class FipeController {

    private final FipePort fipePort;

    public FipeController(FipePort fipePort) {
        this.fipePort = fipePort;
    }

    @GetMapping
    public List<FipeNameAndCode> buscarMarcas() {
        return fipePort.buscarMarcas();
    }

    @GetMapping("/marcas/{brandId}/modelos")
    public List<FipeNameAndCode> buscarModelos(@PathVariable String brandId) {
        return fipePort.buscarModelos(brandId);
    }

    @GetMapping("/marcas/{brandId}/modelos/{modelId}/anos")
    public List<FipeNameAndCode> buscarAnoGasolina(@PathVariable String brandId, @PathVariable String modelId){
        return fipePort.buscarAnos(brandId,modelId);
    }


    @GetMapping("marcas/{brandId}/modelos/{modelId}/anos/{yearId}")
    public FipeVeiculoDTO buscarVeiculoFipe(@PathVariable String brandId, @PathVariable String modelId,@PathVariable String yearId){
        return fipePort.obterPreco(brandId,modelId,yearId);
    }


}
