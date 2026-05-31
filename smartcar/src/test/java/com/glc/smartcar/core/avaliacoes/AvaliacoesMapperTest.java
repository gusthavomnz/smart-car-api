package com.glc.smartcar.core.avaliacoes;

import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoResponseDTO;
import com.glc.smartcar.core.avaliacoes.enums.Conservacao;
import com.glc.smartcar.core.avaliacoes.enums.HistoricoAtivo;
import com.glc.smartcar.core.avaliacoes.enums.StatusResultado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AvaliacoesMapperTest {

    AvaliacoesMapper mapper;

    @BeforeEach
    void configurar() {
        mapper = new AvaliacoesMapper();
    }

    private AvaliacaoRequestDTO criarDtoDeRequisicao() {
        AvaliacaoRequestDTO dto = new AvaliacaoRequestDTO();
        dto.setKmsRodados(50000.0);
        dto.setBrandId("59");
        dto.setModelId("5940");
        dto.setYearId("2020-1");
        dto.setPrecoDesejado(45000.0);
        dto.setNotasPessoais("Carro bem conservado");
        dto.setConservacao(Conservacao.BOM);
        return dto;
    }

    private Avaliacoes criarEntidade() {
        Avaliacoes entidade = new Avaliacoes();
        entidade.setId(1L);
        entidade.setUsuarioId(1L);
        entidade.setFipeId("001234-5");
        entidade.setPrecoDesejado(45000.0);
        entidade.setKmsRodados(50000.0);
        entidade.setPrecoFipe(48000.0);
        entidade.setStatusResultado(StatusResultado.OTIMO_NEGOCIO);
        entidade.setCriado_a(LocalDateTime.of(2024, 1, 15, 10, 0));
        entidade.setConservacao(Conservacao.BOM);
        entidade.setHistoricoAtivo(HistoricoAtivo.SIM);
        entidade.setNotasPessoais("Carro bem conservado");
        return entidade;
    }

    // --- toEntity ---

    @Test
    void deveConverterDtoParaEntidadeCorretamente() {
        AvaliacaoRequestDTO dto = criarDtoDeRequisicao();

        Avaliacoes entidade = mapper.toEntity(dto, 1L, 48000.0, "001234-5", StatusResultado.OTIMO_NEGOCIO, "análise ia");

        assertEquals(1L, entidade.getUsuarioId());
        assertEquals(50000.0, entidade.getKmsRodados());
        assertEquals(48000.0, entidade.getPrecoFipe());
        assertEquals("001234-5", entidade.getFipeId());
        assertEquals(StatusResultado.OTIMO_NEGOCIO, entidade.getStatusResultado());
        assertEquals(Conservacao.BOM, entidade.getConservacao());
        assertEquals("Carro bem conservado", entidade.getNotasPessoais());
    }

    @Test
    void toEntityDeveDefinirHistoricoAtivoComoSim() {
        AvaliacaoRequestDTO dto = criarDtoDeRequisicao();

        Avaliacoes entidade = mapper.toEntity(dto, 1L, 48000.0, "001234-5", StatusResultado.NA_MEDIA, "análise ia");

        assertEquals(HistoricoAtivo.SIM, entidade.getHistoricoAtivo());
    }

    @Test
    void toEntityDevePreencherDataDeCriacao() {
        AvaliacaoRequestDTO dto = criarDtoDeRequisicao();

        Avaliacoes entidade = mapper.toEntity(dto, 1L, 48000.0, "001234-5", StatusResultado.NA_MEDIA, "análise ia");

        assertNotNull(entidade.getCriado_a());
    }

    // --- toDTO(entidade) ---

    @Test
    void deveConverterEntidadeParaDtoSemAnaliseIA() {
        Avaliacoes entidade = criarEntidade();

        AvaliacaoResponseDTO dto = mapper.toDTO(entidade);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getUsuarioId());
        assertEquals("001234-5", dto.getFipeId());
        assertEquals(45000.0, dto.getPrecoDesejado());
        assertEquals(48000.0, dto.getPrecoFipe());
        assertEquals(StatusResultado.OTIMO_NEGOCIO, dto.getStatusResultado());
        assertEquals(Conservacao.BOM, dto.getConservacao());
        assertEquals(HistoricoAtivo.SIM, dto.getHistoricoAtivo());
        assertEquals("Carro bem conservado", dto.getNotasPessoais());
        assertNull(dto.getAvaliacaoIA());
    }

    @Test
    void deveRetornarNullQuandoEntidadeForNull() {
        AvaliacaoResponseDTO dto = mapper.toDTO((Avaliacoes) null);

        assertNull(dto);
    }

    // --- toDTO(entidade, analiseIA) ---

    @Test
    void deveConverterEntidadeParaDtoComAnaliseIA() {
        Avaliacoes entidade = criarEntidade();
        String analise = "Luva de pedreiro compraria sim!";

        AvaliacaoResponseDTO dto = mapper.toDTO(entidade, analise);

        assertNotNull(dto);
        assertEquals("Luva de pedreiro compraria sim!", dto.getAvaliacaoIA());
        assertEquals(1L, dto.getId());
    }

    @Test
    void deveRetornarNullQuandoEntidadeNullComAnaliseIA() {
        AvaliacaoResponseDTO dto = mapper.toDTO((Avaliacoes) null, "alguma analise");

        assertNull(dto);
    }

    // --- toDTOList ---

    @Test
    void deveConverterListaDeEntidadesParaListaDeDtos() {
        List<Avaliacoes> entidades = List.of(criarEntidade(), criarEntidade());

        List<AvaliacaoResponseDTO> dtos = mapper.toDTOList(entidades);

        assertEquals(2, dtos.size());
        assertThat(dtos).allSatisfy(dto -> assertNotNull(dto.getId()));
    }

    @Test
    void deveRetornarListaVaziaQuandoEntradaVazia() {
        List<AvaliacaoResponseDTO> dtos = mapper.toDTOList(List.of());

        assertThat(dtos).isEmpty();
    }
}
