package com.glc.smartcar.fipe.dto;

public record FipeVeiculoDTO(
        String Valor,
        String Marca,
        String Modelo,
        Integer AnoModelo,
        String Combustivel,
        String CodigoFipe,
        String MesReferencia
){}