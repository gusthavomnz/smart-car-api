package com.glc.smartcar.avaliacoes.dto;

import com.glc.smartcar.avaliacoes.enums.Conservacao;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AvaliacaoRequestDTO {

    private Long usuarioId;
    private double kmsRodados;

    private String brandId;
    private String modelId;
    private String yearId;

    private double precoDesejado;

    private String notasPessoais;

    private Conservacao conservacao;

}