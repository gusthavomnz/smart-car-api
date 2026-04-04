package com.glc.smartcar.fipe.adapter;

import com.glc.smartcar.fipe.dto.FipeMarcaDTO;
import com.glc.smartcar.fipe.dto.FipeVeiculoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.glc.smartcar.fipe.dto.FipeModelosResponse;

import java.util.List;

@FeignClient(name = "fipe-api", url = "https://parallelum.com.br/fipe/api/v2")
public interface FipeClient {

    @GetMapping("/cars/brands")
    List<FipeMarcaDTO> buscarMarcas();
    // Esse metodo irá retornar todas marcas de carros disponiveis (RF-04)

    @GetMapping("/cars/brands/{brandId}/models")
    List<FipeModelosResponse> buscarModelos(@PathVariable("brandId") String brandId);
    // Esse metodo consulta todos modelos de carros associados a uma marca. (RF-05)

    @GetMapping("/cars/brands/{brandId}/models/{modelId}/years")
    List<FipeMarcaDTO> buscarAnos(@PathVariable("brandId") String brandId,
                                  @PathVariable("modelId") String modelId);
    // Esse metodo consulta todos os anos/tipos de combustivel do modelo escolhido. (RF-06)

    @GetMapping("/cars/brands/{brandId}/models/{modelId}/years/{yearId}")
    FipeVeiculoDTO obterPreco(@PathVariable("brandId") String brandId,
                              @PathVariable("modelId") String modelId,
                              @PathVariable("yearId") String yearId);
    // Esse metodo retorna todos os detalhes completos e o preço de mercado do veiculo selecionado. (RF-10)

}