package com.glc.smartcar.fipe.dto;

public record FipeInfoResponse(
        String Valor,
        String Marca,
        String Modelo,
        Integer AnoModelo,
        String Combustivel,
        String CodigoFipe,
        String MesReferencia
){}