package com.glc.smartcar.core.avaliacoes.dto;

import com.glc.smartcar.core.avaliacoes.enums.Conservacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AvaliacaoRequestDTO {

    @NotNull(message = "Informe os km's rodados do veículo. ")
    private double kmsRodados;

    @NotNull(message = "Marca não pode ser nula")
    private String brandId;

    @NotBlank(message = "Marca não pode ser nula")
    private String modelId;

    @NotBlank(message = "Marca não pode ser nula")
    private String yearId;

    @NotNull(message = "Informe o preço anunciado no veículo ")
    private double precoDesejado;

    @NotBlank(message = "Informe suas notas pessoais sobre o anuncio. ")
    private String notasPessoais;

    @NotNull(message = "Informe a conservação do veículo")
    private Conservacao conservacao;

}