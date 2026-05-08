package com.glc.smartcar.core.avaliacoes.dto;

import com.glc.smartcar.core.avaliacoes.enums.Conservacao;
import com.glc.smartcar.core.avaliacoes.enums.HistoricoAtivo;
import com.glc.smartcar.core.avaliacoes.enums.StatusResultado;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class AvaliacaoResponseDTO {

    private Long id;
    private Long usuarioId;
    private String fipeId;
    private Double precoDesejado;
    private Double precoFipe;
    private StatusResultado statusResultado;
    private LocalDateTime criado_a;
    private Conservacao conservacao;
    private HistoricoAtivo historicoAtivo;
    private String notasPessoais;
    private String avaliacaoIA;

    public AvaliacaoResponseDTO() {
    }

    public AvaliacaoResponseDTO(Long id, Long usuarioId, String fipeId, Double precoDesejado, Double precoFipe, StatusResultado statusResultado, LocalDateTime criado_a, Conservacao conservacao, HistoricoAtivo historicoAtivo, String notasPessoais) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.fipeId = fipeId;
        this.precoDesejado = precoDesejado;
        this.precoFipe = precoFipe;
        this.statusResultado = statusResultado;
        this.criado_a = criado_a;
        this.conservacao = conservacao;
        this.historicoAtivo = historicoAtivo;
        this.notasPessoais = notasPessoais;
    }

}

