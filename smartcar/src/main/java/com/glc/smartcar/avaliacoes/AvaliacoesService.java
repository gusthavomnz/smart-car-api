package com.glc.smartcar.avaliacoes;

import com.glc.smartcar.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.avaliacoes.dto.AvaliacaoResponseDTO;
import com.glc.smartcar.avaliacoes.enums.HistoricoAtivo;
import com.glc.smartcar.avaliacoes.enums.StatusResultado;
import com.glc.smartcar.fipe.dto.FipeVeiculoDTO;
import com.glc.smartcar.fipe.port.FipePort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service

public class AvaliacoesService {

    private final FipePort fipePort;
    private final AvaliacoesRepository avaliacoesRepository;
    private final AvaliacoesMapper avaliacoesMapper;
    private final ClassificacaoService classificacaoService;

    public AvaliacoesService(FipePort fipePort, AvaliacoesRepository avaliacoesRepository, AvaliacoesMapper avaliacoesMapper, ClassificacaoService classificacaoService) {
        this.fipePort = fipePort;
        this.avaliacoesRepository = avaliacoesRepository;
        this.avaliacoesMapper = avaliacoesMapper;
        this.classificacaoService = classificacaoService;
    }

    @Transactional
    public AvaliacaoResponseDTO criarAvaliacao(AvaliacaoRequestDTO dto) {

        FipeVeiculoDTO fipeVeiculoDTO = fipePort.obterPreco(
                dto.getBrandId(), dto.getModelId(), dto.getYearId()
        );

        double precoFipe = classificacaoService.parseFipe(fipeVeiculoDTO.Valor());
        int converterAno = classificacaoService.parseYearId(dto.getYearId());

        double precoJusto = classificacaoService.calcularPrecoJusto(
                precoFipe,
                converterAno,
                dto.getKmsRodados(),
                dto.getConservacao()
        );

        StatusResultado status = classificacaoService.classificarNegocio(
                dto.getPrecoDesejado(), precoJusto
        );

        Avaliacoes novaAvaliacao = avaliacoesMapper.toEntity(dto,precoFipe, fipeVeiculoDTO.CodigoFipe(), status);

        Avaliacoes salvo = avaliacoesRepository.save(novaAvaliacao);

        return avaliacoesMapper.toDTO(salvo);
    }

    @Transactional
    public AvaliacaoResponseDTO excluirAvaliacao(Long id) {
        Avaliacoes a = avaliacoesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada"));

        a.setHistoricoAtivo(HistoricoAtivo.NAO);
        return avaliacoesMapper.toDTO(avaliacoesRepository.save(a));
    }

}
