package com.glc.smartcar.core.avaliacoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VeiculoDTO {
    private String marca;
    private String modelo;
    private int ano;
    private String combustivel;
}
