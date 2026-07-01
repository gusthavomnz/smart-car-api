package com.glc.smartcar.core.avaliacoes;

import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoResponseDTO;
import com.glc.smartcar.core.avaliacoes.enums.Conservacao;
import com.glc.smartcar.core.avaliacoes.enums.HistoricoAtivo;
import com.glc.smartcar.core.avaliacoes.enums.StatusResultado;
import com.glc.smartcar.integration.fipe.Fipe;
import com.glc.smartcar.integration.fipe.FipeRepository;
import com.glc.smartcar.integration.fipe.dto.FipeVeiculoDTO;
import com.glc.smartcar.integration.fipe.port.FipePort;
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
    @Mock FipeRepository repositorioFipe;
    @Mock AvaliacoesRepository repositorioAvaliacoes;
    
    AvaliacoesMapper mapeador = new AvaliacoesMapper();

    @Mock ClassificacaoService servicoClassificacao;
    @Mock IaPort porteIA;

    @InjectMocks
    AvaliacoesService servicoAvaliacoes;

    private AvaliacaoRequestDTO criarDtoDeRequisicao() {
        AvaliacaoRequestDTO dto = new AvaliacaoRequestDTO();
        dto.setBrandId("59");
        dto.setModelId("5940");
        dto.setYearId("2020-1");
        dto.setKmsRodados(50000.0);
        dto.setPrecoDesejado(45000.0);
        dto.setConservacao(Conservacao.BOM);
        dto.setNotasPessoais("Carro bem conservado");
        return dto;
    }

    private Fipe criarFipe() {
        Fipe fipe = new Fipe();
        fipe.setId(1L);
        fipe.setMarca("Toyota");
        fipe.setModelo("Corolla");
        fipe.setAno(2020);
        fipe.setCodigoFipe("001234-5");
        fipe.setCombustivel("Gasolina");
        fipe.setCodigoMarca("59");
        fipe.setCodigoModelo("5940");
        fipe.setCodigoAno("2020-1");
        return fipe;
    }

    private Avaliacoes criarEntidade() {
        Avaliacoes entidade = new Avaliacoes();
        entidade.setId(1L);
        entidade.setUsuarioId(1L);
        entidade.setFipe(criarFipe());
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
        Fipe fipe = criarFipe();

        when(porteFipe.obterPreco("59", "5940", "2020-1")).thenReturn(veiculoFipe);
        when(servicoClassificacao.parseFipe("R$ 48.000,00")).thenReturn(48000.0);
        when(servicoClassificacao.parseYearId("2020-1")).thenReturn(2020);
        when(servicoClassificacao.calcularPrecoJusto(48000.0, 2020, 50000.0, Conservacao.BOM)).thenReturn(46000.0);
        when(servicoClassificacao.classificarNegocio(45000.0, 46000.0)).thenReturn(StatusResultado.OTIMO_NEGOCIO);
        when(repositorioFipe.findByCodigoMarcaAndCodigoModeloAndCodigoAno("59", "5940", "2020-1")).thenReturn(Optional.of(fipe));
        when(repositorioAvaliacoes.save(any(Avaliacoes.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(porteIA.criarContexto(anyString())).thenReturn(List.of());
        when(porteIA.executarRequisicaoIA(anyList())).thenReturn("Luva compraria!");

        AvaliacaoResponseDTO resultado = servicoAvaliacoes.criarAvaliacao(dto, 1L);

        assertNotNull(resultado);
        assertEquals("001234-5", resultado.getFipeId());
        assertEquals(45000.0, resultado.getPrecoDesejado());
        assertEquals(48000.0, resultado.getPrecoFipe());
        assertEquals(StatusResultado.OTIMO_NEGOCIO, resultado.getStatusResultado());
        assertEquals("Luva compraria!", resultado.getAvaliacaoIA());
        verify(repositorioAvaliacoes).save(any(Avaliacoes.class));
    }

    @Test
    void deveBuscarPrecoNaFipeAoCriarAvaliacao() {
        AvaliacaoRequestDTO dto = criarDtoDeRequisicao();
        FipeVeiculoDTO veiculoFipe = new FipeVeiculoDTO("R$ 48.000,00", "Toyota", "Corolla", 2020, "Gasolina", "001234-5", "Maio 2024");
        Fipe fipe = criarFipe();

        when(porteFipe.obterPreco("59", "5940", "2020-1")).thenReturn(veiculoFipe);
        when(servicoClassificacao.parseFipe(anyString())).thenReturn(48000.0);
        when(servicoClassificacao.parseYearId(anyString())).thenReturn(2020);
        when(servicoClassificacao.calcularPrecoJusto(anyDouble(), anyInt(), anyDouble(), any())).thenReturn(46000.0);
        when(servicoClassificacao.classificarNegocio(anyDouble(), anyDouble())).thenReturn(StatusResultado.NA_MEDIA);
        when(repositorioFipe.findByCodigoMarcaAndCodigoModeloAndCodigoAno("59", "5940", "2020-1")).thenReturn(Optional.of(fipe));
        when(repositorioAvaliacoes.save(any(Avaliacoes.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(porteIA.criarContexto(anyString())).thenReturn(List.of());
        when(porteIA.executarRequisicaoIA(anyList())).thenReturn("Análise da IA");

        servicoAvaliacoes.criarAvaliacao(dto, 1L);

        verify(porteFipe).obterPreco("59", "5940", "2020-1");
    }

    // --- excluirAvaliacao ---

    @Test
    void deveDesativarAvaliacaoComSucesso() {
        Avaliacoes entidade = criarEntidade();

        when(repositorioAvaliacoes.findById(1L)).thenReturn(Optional.of(entidade));
        when(repositorioAvaliacoes.save(any(Avaliacoes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AvaliacaoResponseDTO resultado = servicoAvaliacoes.excluirAvaliacao(1L, 1L);

        assertNotNull(resultado);
        assertEquals(HistoricoAtivo.NAO, entidade.getHistoricoAtivo());
    }

    @Test
    void deveLancarExcecaoQuandoAvaliacaoNaoEncontrada() {
        when(repositorioAvaliacoes.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoAvaliacoes.excluirAvaliacao(99L, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Avaliação não encontrada");
    }

    @Test
    void deveDefinirHistoricoAtivoComoNaoAoExcluir() {
        Avaliacoes entidade = criarEntidade();
        when(repositorioAvaliacoes.findById(1L)).thenReturn(Optional.of(entidade));
        when(repositorioAvaliacoes.save(any(Avaliacoes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        servicoAvaliacoes.excluirAvaliacao(1L, 1L);

        assertEquals(HistoricoAtivo.NAO, entidade.getHistoricoAtivo());
    }

    // --- listarAvaliacoesPorUsuario ---

    @Test
    void deveListarAvaliacoesAtivasDoUsuario() {
        List<Avaliacoes> entidades = List.of(criarEntidade(), criarEntidade());

        when(repositorioAvaliacoes.findAllByUsuarioIdAndHistoricoAtivo(1L, HistoricoAtivo.SIM)).thenReturn(entidades);

        List<AvaliacaoResponseDTO> resultado = servicoAvaliacoes.listarAvaliacoesPorUsuario(1L);

        assertEquals(2, resultado.size());
        verify(repositorioAvaliacoes).findAllByUsuarioIdAndHistoricoAtivo(1L, HistoricoAtivo.SIM);
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioSemAvaliacoes() {
        when(repositorioAvaliacoes.findAllByUsuarioIdAndHistoricoAtivo(99L, HistoricoAtivo.SIM)).thenReturn(List.of());

        List<AvaliacaoResponseDTO> resultado = servicoAvaliacoes.listarAvaliacoesPorUsuario(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveBuscarApenasAvaliacoesAtivasNaListagem() {
        when(repositorioAvaliacoes.findAllByUsuarioIdAndHistoricoAtivo(1L, HistoricoAtivo.SIM)).thenReturn(List.of());

        servicoAvaliacoes.listarAvaliacoesPorUsuario(1L);

        verify(repositorioAvaliacoes).findAllByUsuarioIdAndHistoricoAtivo(1L, HistoricoAtivo.SIM);
        verify(repositorioAvaliacoes, never()).findAllByUsuarioIdAndHistoricoAtivo(1L, HistoricoAtivo.NAO);
    }
}
