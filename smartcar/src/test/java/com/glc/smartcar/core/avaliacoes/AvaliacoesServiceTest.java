package com.glc.smartcar.core.avaliacoes;

import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoResponseDTO;
import com.glc.smartcar.core.avaliacoes.enums.Conservacao;
import com.glc.smartcar.core.avaliacoes.enums.HistoricoAtivo;
import com.glc.smartcar.core.avaliacoes.enums.StatusResultado;
import com.glc.smartcar.integration.fipe.dto.FipeVeiculoDTO;
import com.glc.smartcar.integration.fipe.port.FipePort;
import com.glc.smartcar.integration.ia.dto.Message;
import com.glc.smartcar.integration.ia.port.IaPort;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacoesServiceTest {

    @Mock FipePort porteFipe;
    @Mock AvaliacoesRepository repositorioAvaliacoes;
    @Mock AvaliacoesMapper mapeador;
    @Mock ClassificacaoService servicoClassificacao;
    @Mock IaPort porteIA;

    @InjectMocks
    AvaliacoesService servicoAvaliacoes;

    private AvaliacaoRequestDTO criarDtoDeRequisicao() {
        AvaliacaoRequestDTO dto = new AvaliacaoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setBrandId("59");
        dto.setModelId("5940");
        dto.setYearId("2020-1");
        dto.setKmsRodados(50000.0);
        dto.setPrecoDesejado(45000.0);
        dto.setConservacao(Conservacao.BOM);
        dto.setNotasPessoais("Carro bem conservado");
        return dto;
    }

    private Avaliacoes criarEntidade() {
        Avaliacoes entidade = new Avaliacoes();
        entidade.setId(1L);
        entidade.setUsuarioId(1L);
        entidade.setFipeId("001234-5");
        entidade.setPrecoDesejado(45000.0);
        entidade.setPrecoFipe(48000.0);
        entidade.setKmsRodados(50000.0);
        entidade.setStatusResultado(StatusResultado.OTIMO_NEGOCIO);
        entidade.setCriado_a(LocalDateTime.now());
        entidade.setConservacao(Conservacao.BOM);
        entidade.setHistoricoAtivo(HistoricoAtivo.SIM);
        entidade.setNotasPessoais("Carro bem conservado");
        return entidade;
    }

    // --- criarAvaliacao ---

    @Test
    void deveCriarAvaliacaoComSucesso() {
        AvaliacaoRequestDTO dto = criarDtoDeRequisicao();
        FipeVeiculoDTO veiculoFipe = new FipeVeiculoDTO("R$ 48.000,00", "Toyota", "Corolla", 2020, "Gasolina", "001234-5", "Maio 2024");
        Avaliacoes entidade = criarEntidade();
        AvaliacaoResponseDTO respostaEsperada = new AvaliacaoResponseDTO();

        when(porteFipe.obterPreco("59", "5940", "2020-1")).thenReturn(veiculoFipe);
        when(servicoClassificacao.parseFipe("R$ 48.000,00")).thenReturn(48000.0);
        when(servicoClassificacao.parseYearId("2020-1")).thenReturn(2020);
        when(servicoClassificacao.calcularPrecoJusto(48000.0, 2020, 50000.0, Conservacao.BOM)).thenReturn(46000.0);
        when(servicoClassificacao.classificarNegocio(45000.0, 46000.0)).thenReturn(StatusResultado.OTIMO_NEGOCIO);
        when(mapeador.toEntity(dto, 48000.0, "001234-5", StatusResultado.OTIMO_NEGOCIO)).thenReturn(entidade);
        when(repositorioAvaliacoes.save(entidade)).thenReturn(entidade);
        when(porteIA.criarContexto(anyString())).thenReturn(List.of());
        when(porteIA.executarRequisicaoIA(anyList())).thenReturn("Luva compraria!");
        when(mapeador.toDTO(entidade, "Luva compraria!")).thenReturn(respostaEsperada);

        AvaliacaoResponseDTO resultado = servicoAvaliacoes.criarAvaliacao(dto);

        assertNotNull(resultado);
        verify(repositorioAvaliacoes).save(entidade);
        verify(porteIA).criarContexto(anyString());
        verify(porteIA).executarRequisicaoIA(anyList());
    }

    @Test
    void deveBuscarPrecoNaFipeAoCriarAvaliacao() {
        AvaliacaoRequestDTO dto = criarDtoDeRequisicao();
        FipeVeiculoDTO veiculoFipe = new FipeVeiculoDTO("R$ 48.000,00", "Toyota", "Corolla", 2020, "Gasolina", "001234-5", "Maio 2024");
        Avaliacoes entidade = criarEntidade();

        when(porteFipe.obterPreco("59", "5940", "2020-1")).thenReturn(veiculoFipe);
        when(servicoClassificacao.parseFipe(anyString())).thenReturn(48000.0);
        when(servicoClassificacao.parseYearId(anyString())).thenReturn(2020);
        when(servicoClassificacao.calcularPrecoJusto(anyDouble(), anyInt(), anyDouble(), any())).thenReturn(46000.0);
        when(servicoClassificacao.classificarNegocio(anyDouble(), anyDouble())).thenReturn(StatusResultado.NA_MEDIA);
        when(mapeador.toEntity(any(), anyDouble(), anyString(), any())).thenReturn(entidade);
        when(repositorioAvaliacoes.save(any())).thenReturn(entidade);
        when(porteIA.criarContexto(anyString())).thenReturn(List.of());
        when(porteIA.executarRequisicaoIA(anyList())).thenReturn("Análise da IA");
        when(mapeador.toDTO(any(Avaliacoes.class), anyString())).thenReturn(new AvaliacaoResponseDTO());

        servicoAvaliacoes.criarAvaliacao(dto);

        verify(porteFipe).obterPreco("59", "5940", "2020-1");
    }

    // --- excluirAvaliacao ---

    @Test
    void deveDesativarAvaliacaoComSucesso() {
        Avaliacoes entidade = criarEntidade();
        Avaliacoes entidadeSalva = criarEntidade();
        entidadeSalva.setHistoricoAtivo(HistoricoAtivo.NAO);
        AvaliacaoResponseDTO respostaEsperada = new AvaliacaoResponseDTO();

        when(repositorioAvaliacoes.findById(1L)).thenReturn(Optional.of(entidade));
        when(repositorioAvaliacoes.save(entidade)).thenReturn(entidadeSalva);
        when(mapeador.toDTO(entidadeSalva)).thenReturn(respostaEsperada);

        AvaliacaoResponseDTO resultado = servicoAvaliacoes.excluirAvaliacao(1L);

        assertNotNull(resultado);
        assertEquals(HistoricoAtivo.NAO, entidade.getHistoricoAtivo());
        verify(repositorioAvaliacoes).save(entidade);
    }

    @Test
    void deveLancarExcecaoQuandoAvaliacaoNaoEncontrada() {
        when(repositorioAvaliacoes.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoAvaliacoes.excluirAvaliacao(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Avaliação não encontrada");
    }

    @Test
    void deveDefinirHistoricoAtivoComoNaoAoExcluir() {
        Avaliacoes entidade = criarEntidade();
        when(repositorioAvaliacoes.findById(1L)).thenReturn(Optional.of(entidade));
        when(repositorioAvaliacoes.save(entidade)).thenReturn(entidade);
        when(mapeador.toDTO(entidade)).thenReturn(new AvaliacaoResponseDTO());

        servicoAvaliacoes.excluirAvaliacao(1L);

        assertEquals(HistoricoAtivo.NAO, entidade.getHistoricoAtivo());
    }

    // --- listarAvaliacoesPorUsuario ---

    @Test
    void deveListarAvaliacoesAtivasDoUsuario() {
        List<Avaliacoes> entidades = List.of(criarEntidade(), criarEntidade());
        List<AvaliacaoResponseDTO> listaEsperada = List.of(new AvaliacaoResponseDTO(), new AvaliacaoResponseDTO());

        when(repositorioAvaliacoes.findAllByUsuarioIdAndHistoricoAtivo(1L, HistoricoAtivo.SIM)).thenReturn(entidades);
        when(mapeador.toDTOList(entidades)).thenReturn(listaEsperada);

        List<AvaliacaoResponseDTO> resultado = servicoAvaliacoes.listarAvaliacoesPorUsuario(1L);

        assertEquals(2, resultado.size());
        verify(repositorioAvaliacoes).findAllByUsuarioIdAndHistoricoAtivo(1L, HistoricoAtivo.SIM);
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioSemAvaliacoes() {
        when(repositorioAvaliacoes.findAllByUsuarioIdAndHistoricoAtivo(99L, HistoricoAtivo.SIM)).thenReturn(List.of());
        when(mapeador.toDTOList(List.of())).thenReturn(List.of());

        List<AvaliacaoResponseDTO> resultado = servicoAvaliacoes.listarAvaliacoesPorUsuario(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveBuscarApenasAvaliacoesAtivasNaListagem() {
        when(repositorioAvaliacoes.findAllByUsuarioIdAndHistoricoAtivo(1L, HistoricoAtivo.SIM)).thenReturn(List.of());
        when(mapeador.toDTOList(any())).thenReturn(List.of());

        servicoAvaliacoes.listarAvaliacoesPorUsuario(1L);

        verify(repositorioAvaliacoes).findAllByUsuarioIdAndHistoricoAtivo(1L, HistoricoAtivo.SIM);
        verify(repositorioAvaliacoes, never()).findAllByUsuarioIdAndHistoricoAtivo(1L, HistoricoAtivo.NAO);
    }
}
