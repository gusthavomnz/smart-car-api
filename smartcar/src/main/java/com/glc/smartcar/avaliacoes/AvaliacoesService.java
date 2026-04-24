package com.glc.smartcar.avaliacoes;

import com.glc.smartcar.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.avaliacoes.dto.AvaliacaoResponseDTO;
import com.glc.smartcar.avaliacoes.enums.HistoricoAtivo;
import com.glc.smartcar.avaliacoes.enums.StatusResultado;
import com.glc.smartcar.fipe.dto.FipeVeiculoDTO;
import com.glc.smartcar.fipe.port.FipePort;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service

public class AvaliacoesService {

    private final FipePort fipePort;
    private final AvaliacoesRepository avaliacoesRepository;

    @Autowired
    private ClassificacaoService classificacaoService;

    public AvaliacoesService(FipePort fipePort, AvaliacoesRepository avaliacoesRepository) {
        this.fipePort = fipePort;
        this.avaliacoesRepository = avaliacoesRepository;
    }

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

        Avaliacoes novaAvaliacao = new Avaliacoes();
        novaAvaliacao.setUsuarioId(dto.getUsuarioId());
        novaAvaliacao.setFipeId(fipeVeiculoDTO.CodigoFipe());
        novaAvaliacao.setConservacao(dto.getConservacao());
        novaAvaliacao.setHistoricoAtivo(HistoricoAtivo.SIM);
        novaAvaliacao.setNotasPessoais(dto.getNotasPessoais());
        novaAvaliacao.setPrecoDesejado(dto.getPrecoDesejado());
        novaAvaliacao.setPrecoFipe(precoFipe);
        novaAvaliacao.setStatusResultado(status);
        novaAvaliacao.setCriado_a(LocalDateTime.now());
        novaAvaliacao.setKmsRodados(dto.getKmsRodados());

        Avaliacoes salvo = avaliacoesRepository.save(novaAvaliacao);

        // Mapeamento direto/Depois refatorar.
        return new AvaliacaoResponseDTO(
                salvo.getId(),
                salvo.getUsuarioId(),
                salvo.getFipeId(),
                salvo.getPrecoDesejado(),
                salvo.getPrecoFipe(),
                salvo.getStatusResultado(),
                salvo.getCriado_a(),
                salvo.getConservacao(),
                salvo.getHistoricoAtivo(),
                salvo.getNotasPessoais()
        );
    }



}
