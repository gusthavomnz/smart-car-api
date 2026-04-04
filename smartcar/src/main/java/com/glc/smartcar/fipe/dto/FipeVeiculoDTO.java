package com.glc.smartcar.fipe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FipeVeiculoDTO(

        @JsonProperty ("price")
        String Valor,

        @JsonProperty("brand")
        String Marca,

        @JsonProperty("model")
        String Modelo,

        @JsonProperty("modelYear")
        Integer AnoModelo,

        @JsonProperty("fuel")
        String Combustivel,

        @JsonProperty("codeFipe")
        String CodigoFipe,

        @JsonProperty("referenceMonth")
        String MesReferencia

) {}