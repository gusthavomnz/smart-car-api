package com.glc.smartcar.avaliacoes;


import com.glc.smartcar.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.avaliacoes.dto.AvaliacaoResponseDTO;
import com.glc.smartcar.avaliacoes.enums.HistoricoAtivo;
import com.glc.smartcar.avaliacoes.enums.StatusResultado;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AvaliacoesMapper {



    public Avaliacoes toEntity(AvaliacaoRequestDTO dto, double precoFipe, String codigoFipe, StatusResultado status) {
        Avaliacoes entity = new Avaliacoes();


        entity.setUsuarioId(dto.getUsuarioId());
        entity.setPrecoDesejado(dto.getPrecoDesejado());
        entity.setKmsRodados(dto.getKmsRodados());
        entity.setConservacao(dto.getConservacao());
        entity.setNotasPessoais(dto.getNotasPessoais());

        entity.setPrecoFipe(precoFipe);
        entity.setFipeId(codigoFipe);
        entity.setStatusResultado(status);

        entity.setHistoricoAtivo(HistoricoAtivo.SIM);
        entity.setCriado_a(LocalDateTime.now());

        return entity;
    }



    public AvaliacaoResponseDTO toDTO(Avaliacoes entity){
        if (entity == null) {
            return null;
        }
      return new AvaliacaoResponseDTO(
              entity.getId(),
              entity.getUsuarioId(),
              entity.getFipeId(),
              entity.getPrecoDesejado(),
              entity.getPrecoFipe(),
              entity.getStatusResultado(),
              entity.getCriado_a(),
              entity.getConservacao(),
              entity.getHistoricoAtivo(),
              entity.getNotasPessoais());
    }
}
