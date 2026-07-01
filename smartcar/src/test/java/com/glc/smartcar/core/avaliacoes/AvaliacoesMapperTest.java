package com.glc.smartcar.core.avaliacoes;

import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoResponseDTO;
import com.glc.smartcar.core.avaliacoes.enums.Conservacao;
import com.glc.smartcar.core.avaliacoes.enums.HistoricoAtivo;
import com.glc.smartcar.core.avaliacoes.enums.StatusResultado;
import com.glc.smartcar.integration.fipe.Fipe;
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
        entidade.setKmsRodados(50000.0);
        entidade.setPrecoFipe(48000.0);
        entidade.setStatusResultado(StatusResultado.OTIMO_NEGOCIO);
        entidade.setCriado_a(LocalDateTime.of(2024, 1, 15, 10, 0));
        entidade.setConservacao(Conservacao.BOM);
        entidade.setHistoricoAtivo(HistoricoAtivo.SIM);
        entidade.setNotasPessoais("Carro bem conservado");
        entidade.setAvaliacaoIa("Luva de pedreiro compraria sim!");
        entidade.setVariacao(-6.25);
        return entidade;
    }

    // --- toEntity ---

    @Test
    void deveConverterDtoParaEntidadeCorretamente() {
        AvaliacaoRequestDTO dto = criarDtoDeRequisicao();
        Fipe fipe = criarFipe();

        Avaliacoes entidade = mapper.toEntity(dto, 1L, 48000.0, fipe, StatusResultado.OTIMO_NEGOCIO, "análise ia", -6.25);

        assertEquals(1L, entidade.getUsuarioId());
        assertEquals(50000.0, entidade.getKmsRodados());
        assertEquals(48000.0, entidade.getPrecoFipe());
        assertEquals(fipe, entidade.getFipe());
        assertEquals(StatusResultado.OTIMO_NEGOCIO, entidade.getStatusResultado());
        assertEquals(Conservacao.BOM, entidade.getConservacao());
        assertEquals("Carro bem conservado", entidade.getNotasPessoais());
        assertEquals("análise ia", entidade.getAvaliacaoIa());
        assertEquals(-6.25, entidade.getVariacao());
    }

    @Test
    void toEntityDeveDefinirHistoricoAtivoComoSim() {
        AvaliacaoRequestDTO dto = criarDtoDeRequisicao();
        Fipe fipe = criarFipe();

        Avaliacoes entidade = mapper.toEntity(dto, 1L, 48000.0, fipe, StatusResultado.NA_MEDIA, "análise ia", 0.0);

        assertEquals(HistoricoAtivo.SIM, entidade.getHistoricoAtivo());
    }

    @Test
    void toEntityDevePreencherDataDeCriacao() {
        AvaliacaoRequestDTO dto = criarDtoDeRequisicao();
        Fipe fipe = criarFipe();

        Avaliacoes entidade = mapper.toEntity(dto, 1L, 48000.0, fipe, StatusResultado.NA_MEDIA, "análise ia", 0.0);

        assertNotNull(entidade.getCriado_a());
    }

    // --- toDTO ---

    @Test
    void deveConverterEntidadeParaDtoCorretamente() {
        Avaliacoes entidade = criarEntidade();

        AvaliacaoResponseDTO dto = mapper.toDTO(entidade);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("001234-5", dto.getFipeId());
        assertEquals(45000.0, dto.getPrecoDesejado());
        assertEquals(48000.0, dto.getPrecoFipe());
        assertEquals(StatusResultado.OTIMO_NEGOCIO, dto.getStatusResultado());
        assertEquals(Conservacao.BOM, dto.getConservacao());
        assertEquals(HistoricoAtivo.SIM, dto.getHistoricoAtivo());
        assertEquals("Carro bem conservado", dto.getNotasPessoais());
        assertEquals("Luva de pedreiro compraria sim!", dto.getAvaliacaoIA());
        assertEquals(-6.25, dto.getVariacao());
    }

    @Test
    void deveRetornarNullQuandoEntidadeForNull() {
        AvaliacaoResponseDTO dto = mapper.toDTO(null);

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
