package com.glc.smartcar.core.avaliacoes;

import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoResponseDTO;
import com.glc.smartcar.core.avaliacoes.dto.VeiculoDTO;
import com.glc.smartcar.core.avaliacoes.enums.HistoricoAtivo;
import com.glc.smartcar.core.avaliacoes.enums.StatusResultado;
import com.glc.smartcar.integration.fipe.Fipe;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Component
public class AvaliacoesMapper {

    public Avaliacoes toEntity(AvaliacaoRequestDTO dto, Long usuarioId, double precoFipe, Fipe fipe, StatusResultado status, String msgIA, double variacao) {
        Avaliacoes entity = new Avaliacoes();

        entity.setUsuarioId(usuarioId);
        entity.setPrecoDesejado(dto.getPrecoDesejado());
        entity.setKmsRodados(dto.getKmsRodados());
        entity.setConservacao(dto.getConservacao());
        entity.setNotasPessoais(dto.getNotasPessoais());

        entity.setPrecoFipe(precoFipe);
        entity.setFipe(fipe);
        entity.setStatusResultado(status);

        entity.setHistoricoAtivo(HistoricoAtivo.SIM);
        entity.setCriado_a(LocalDateTime.now());
        entity.setAvaliacaoIa(msgIA);
        entity.setVariacao(variacao);

        return entity;
    }

    public AvaliacaoResponseDTO toDTO(Avaliacoes entity) {
        if (entity == null) {
            return null;
        }
        Fipe fipe = entity.getFipe();
        VeiculoDTO veiculo = fipe != null
                ? new VeiculoDTO(fipe.getMarca(), fipe.getModelo(), fipe.getAno(), fipe.getCombustivel())
                : null;

        return new AvaliacaoResponseDTO(
                entity.getId(),
                fipe != null ? fipe.getCodigoFipe() : null,
                veiculo,
                entity.getPrecoDesejado(),
                entity.getPrecoFipe(),
                entity.getVariacao(),
                entity.getStatusResultado(),
                entity.getConservacao(),
                entity.getKmsRodados(),
                entity.getNotasPessoais(),
                entity.getAvaliacaoIa(),
                entity.getCriado_a(),
                entity.getHistoricoAtivo()
        );
    }

    public List<AvaliacaoResponseDTO> toDTOList(List<Avaliacoes> entities) {
        return entities.stream()
                .map(this::toDTO)
                .toList();
    }
}
