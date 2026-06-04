package com.glc.smartcar.core.avaliacoes;

import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoResponseDTO;
import com.glc.smartcar.core.avaliacoes.enums.HistoricoAtivo;
import com.glc.smartcar.core.avaliacoes.enums.StatusResultado;
import com.glc.smartcar.integration.fipe.Fipe;
import com.glc.smartcar.integration.fipe.FipeRepository;
import com.glc.smartcar.integration.fipe.dto.FipeVeiculoDTO;
import com.glc.smartcar.integration.fipe.port.FipePort;
import com.glc.smartcar.integration.ia.port.IaPort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
public class AvaliacoesService {

    private final FipePort fipePort;
    private final FipeRepository fipeRepository;
    private final AvaliacoesRepository avaliacoesRepository;
    private final AvaliacoesMapper avaliacoesMapper;
    private final ClassificacaoService classificacaoService;
    private final IaPort iaPort;

    public AvaliacoesService(FipePort fipePort, FipeRepository fipeRepository, AvaliacoesRepository avaliacoesRepository, AvaliacoesMapper avaliacoesMapper, ClassificacaoService classificacaoService, IaPort iaPort) {
        this.fipePort = fipePort;
        this.fipeRepository = fipeRepository;
        this.avaliacoesRepository = avaliacoesRepository;
        this.avaliacoesMapper = avaliacoesMapper;
        this.classificacaoService = classificacaoService;
        this.iaPort = iaPort;
    }


    @Transactional
    public AvaliacaoResponseDTO criarAvaliacao(AvaliacaoRequestDTO dto, Long usuarioId) {

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

        String mensagemFinal = processarAnaliseIa(fipeVeiculoDTO.Modelo(), dto, precoJusto, precoFipe, status);
        double variacao = calcularVariacao(dto.getPrecoDesejado(), precoFipe);
        Avaliacoes novaAvaliacao = avaliacoesMapper.toEntity(dto, usuarioId, precoFipe, fipeVeiculoDTO.CodigoFipe(), status, mensagemFinal, variacao);
        Avaliacoes salvo = avaliacoesRepository.save(novaAvaliacao);

        Fipe fipe = fipeRepository.findByCodigoFipe(salvo.getFipeId()).orElse(null);
        return avaliacoesMapper.toDTO(salvo, fipe);
    }

    private double calcularVariacao(double precoDesejado, double precoFipe) {
        double variacao = ((precoDesejado - precoFipe) / precoFipe) * 100;
        return Math.round(variacao * 100.0) / 100.0;
    }

    private String processarAnaliseIa(String modelo, AvaliacaoRequestDTO dto, double justo, double fipe, StatusResultado status) {
        String contexto = String.format(
                "Veículo: %s. Status: %s. Conservação: %s. Anunciado: R$%.2f. Justo: R$%.2f. Fipe: R$%.2f. KM: %.0f.",
                modelo, status, dto.getConservacao(), dto.getPrecoDesejado(), justo, fipe, dto.getKmsRodados()
        );

        return iaPort.executarRequisicaoIA(iaPort.criarContexto(contexto));
    }


    @Transactional
    public AvaliacaoResponseDTO excluirAvaliacao(Long id, Long usuarioId) {
        Avaliacoes a = avaliacoesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada"));

        if (!a.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir esta avaliação");
        }

        a.setHistoricoAtivo(HistoricoAtivo.NAO);
        Fipe fipe = fipeRepository.findByCodigoFipe(a.getFipeId()).orElse(null);
        return avaliacoesMapper.toDTO(avaliacoesRepository.save(a), fipe);
    }


    public List<AvaliacaoResponseDTO> listarAvaliacoesPorUsuario(Long usuarioId) {
        List<Avaliacoes> listaEntities = avaliacoesRepository
                .findAllByUsuarioIdAndHistoricoAtivo(usuarioId, HistoricoAtivo.SIM);

        return avaliacoesMapper.toDTOList(listaEntities,
                codigoFipe -> fipeRepository.findByCodigoFipe(codigoFipe).orElse(null));
    }

}
