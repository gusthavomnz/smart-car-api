package com.glc.smartcar.avaliacoes;

import com.glc.smartcar.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.avaliacoes.dto.AvaliacaoResponseDTO;
import com.glc.smartcar.avaliacoes.enums.HistoricoAtivo;
import com.glc.smartcar.avaliacoes.enums.StatusResultado;
import com.glc.smartcar.fipe.dto.FipeVeiculoDTO;
import com.glc.smartcar.fipe.port.FipePort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class AvaliacoesService {

    private final FipePort fipePort;
    private final AvaliacoesRepository avaliacoesRepository;

    public AvaliacoesService(FipePort fipePort, AvaliacoesRepository avaliacoesRepository) {
        this.fipePort = fipePort;
        this.avaliacoesRepository = avaliacoesRepository;
    }



    public AvaliacaoResponseDTO criarAvaliacao(AvaliacaoRequestDTO avaliacaoRequestDTO) {
        FipeVeiculoDTO fipeVeiculoDTO = fipePort.obterPreco(
                avaliacaoRequestDTO.getBrandId(),
                avaliacaoRequestDTO.getModelId(),
                avaliacaoRequestDTO.getYearId()
        );

        // converte o valor FIPE de String para double
        double precoFipe = Double.parseDouble(
                fipeVeiculoDTO.Valor()
                        .replace("R$ ", "")
                        .replace(".", "")
                        .replace(",", ".")
        );

        // chama uma vez e guarda o resultado
        StatusResultado status = avaliacaoPreco(avaliacaoRequestDTO.getPrecoDesejado(), precoFipe);

        Avaliacoes novaAvaliacao = new Avaliacoes();
        novaAvaliacao.setUsuarioId(avaliacaoRequestDTO.getUsuarioId());
        novaAvaliacao.setFipeId(fipeVeiculoDTO.CodigoFipe());
        novaAvaliacao.setConservacao(avaliacaoRequestDTO.getConservacao());
        novaAvaliacao.setHistoricoAtivo(HistoricoAtivo.SIM);
        novaAvaliacao.setNotasPessoais(avaliacaoRequestDTO.getNotasPessoais());
        novaAvaliacao.setPrecoDesejado(avaliacaoRequestDTO.getPrecoDesejado());
        novaAvaliacao.setPrecoFipe(precoFipe);
        novaAvaliacao.setStatusResultado(status);
        novaAvaliacao.setCriado_a(LocalDateTime.now());

        Avaliacoes salvo = avaliacoesRepository.save(novaAvaliacao);

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


    public StatusResultado avaliacaoPreco(double precoDesejado, double precoFipe) {
        var calculo = ((precoDesejado - precoFipe) / precoFipe) * 100;

        if (calculo <= -5) {
            return StatusResultado.OTIMO_NEGOCIO;
        } else if (calculo <= 5) {
            return StatusResultado.NA_MEDIA;
        } else if (calculo <= 15) {
            return StatusResultado.ACIMA_DA_MEDIA;
        } else {
            return StatusResultado.DIFICIL_DE_VENDER;
        }
    }
}
