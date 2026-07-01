package com.glc.smartcar.core.avaliacoes.dto;

import com.glc.smartcar.core.avaliacoes.enums.Conservacao;
import com.glc.smartcar.core.avaliacoes.enums.HistoricoAtivo;
import com.glc.smartcar.core.avaliacoes.enums.StatusResultado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvaliacaoResponseDTO {

    private Long id;
    private String fipeId;
    private VeiculoDTO veiculo;
    private Double precoDesejado;
    private Double precoFipe;
    private Double variacao;
    private StatusResultado statusResultado;
    private Conservacao conservacao;
    private Double kmsRodados;
    private String notasPessoais;
    private String avaliacaoIA;
    private LocalDateTime criado_a;
    private HistoricoAtivo historicoAtivo;

}
