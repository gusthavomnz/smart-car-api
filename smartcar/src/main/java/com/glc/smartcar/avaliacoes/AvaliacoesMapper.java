package com.glc.smartcar.avaliacoes;


import com.glc.smartcar.avaliacoes.dto.AvaliacaoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AvaliacoesMapper {


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
