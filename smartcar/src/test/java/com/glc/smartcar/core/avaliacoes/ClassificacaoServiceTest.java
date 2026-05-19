package com.glc.smartcar.core.avaliacoes;

import com.glc.smartcar.core.avaliacoes.enums.Conservacao;
import com.glc.smartcar.core.avaliacoes.enums.StatusResultado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ClassificacaoServiceTest {

    @InjectMocks
    ClassificacaoService classificacaoService;

    // --- calcularIdade ---

    @Test
    void deveRetornarIdadeDoCarro() {
        assertEquals(2, classificacaoService.calcularIdade(2024, 2026));
    }

    @Test
    void deveRetornarZeroParaCarrosFabricadosAnoSeguinte() {
        assertEquals(0, classificacaoService.calcularIdade(2027, 2026));
    }

    @Test
    void deveRetornarZeroParaCarroDoMesmoAno() {
        assertEquals(0, classificacaoService.calcularIdade(2024, 2024));
    }

    // --- parseFipe ---

    @Test
    void deveConverterPrecoFipeParaDouble() {
        assertEquals(50000.00, classificacaoService.parseFipe("R$ 50.000,00"), 0.01);
    }

    @Test
    void deveConverterPrecoFipeComMilharesECentavos() {
        assertEquals(123456.78, classificacaoService.parseFipe("R$ 123.456,78"), 0.01);
    }

    // --- parseYearId ---

    @Test
    void deveExtrairAnoDoYearId() {
        assertEquals(2025, classificacaoService.parseYearId("2025-1"));
    }

    @Test
    void deveExtrairAnoDoYearIdComDiferenteCombustivel() {
        assertEquals(2020, classificacaoService.parseYearId("2020-3"));
    }

    // --- calcularKmEsperado ---

    @Test
    void deveCalcularKmEsperadoPara3Anos() {
        assertEquals(42000.0, classificacaoService.calcularKmEsperado(3), 0.01);
    }

    @Test
    void deveRetornarZeroKmEsperadoParaCarroNovo() {
        assertEquals(0.0, classificacaoService.calcularKmEsperado(0), 0.01);
    }

    // --- calcularFatorKm ---

    @Test
    void deveFatorKmSerUmQuandoKmIgualAoEsperado() {
        assertEquals(1.0, classificacaoService.calcularFatorKm(50000.0, 50000.0), 0.0001);
    }

    @Test
    void deveFatorKmSerClampadoAoMinimoQuandoMuitoRodado() {
        // exp(-0.00002 * 200000) = exp(-4) ≈ 0.018, clamped to 0.60
        assertEquals(0.60, classificacaoService.calcularFatorKm(300000.0, 100000.0), 0.0001);
    }

    @Test
    void deveFatorKmSerClampadoAoMaximoQuandoPoucoRodado() {
        // exp(-0.00002 * (-200000)) = exp(4) ≈ 54.6, clamped to 1.15
        assertEquals(1.15, classificacaoService.calcularFatorKm(0.0, 200000.0), 0.0001);
    }

    @Test
    void deveFatorKmAplicarPenalidadeQuandoKmAcimaDoEsperado() {
        double fator = classificacaoService.calcularFatorKm(30000.0, 20000.0);
        assertThat(fator).isLessThan(1.0).isGreaterThanOrEqualTo(0.60);
    }

    @Test
    void deveFatorKmAplicarBonusQuandoKmAbaixoDoEsperado() {
        double fator = classificacaoService.calcularFatorKm(10000.0, 20000.0);
        assertThat(fator).isGreaterThan(1.0).isLessThanOrEqualTo(1.15);
    }

    // --- calcularFatorConservacao ---

    @Test
    void deveFatorConservacaoNovoSerMaximo() {
        // NOVO (1.0): 1.0 + 0.4*(1.0-0.5) = 1.20
        assertEquals(1.20, classificacaoService.calcularFatorConservacao(Conservacao.NOVO), 0.001);
    }

    @Test
    void deveFatorConservacaoRegularSerNeutro() {
        // REGULAR (0.5): 1.0 + 0.4*(0.5-0.5) = 1.0
        assertEquals(1.0, classificacaoService.calcularFatorConservacao(Conservacao.REGULAR), 0.001);
    }

    @Test
    void deveFatorConservacaoBomSerPositivo() {
        // BOM (0.75): 1.0 + 0.4*(0.75-0.5) = 1.10
        assertEquals(1.10, classificacaoService.calcularFatorConservacao(Conservacao.BOM), 0.001);
    }

    @Test
    void deveFatorConservacaoRuimReduzirPreco() {
        // RUIM (0.25): 1.0 + 0.4*(0.25-0.5) = 0.90
        assertEquals(0.90, classificacaoService.calcularFatorConservacao(Conservacao.RUIM), 0.001);
    }

    // --- classificarNegocio ---

    @Test
    void deveClassificarComoOtimoNegocioQuandoPrecoMuitoBaixo() {
        // ratio = 80000 / 100000 = 0.80 → OTIMO_NEGOCIO
        assertEquals(StatusResultado.OTIMO_NEGOCIO,
                classificacaoService.classificarNegocio(80000.0, 100000.0));
    }

    @Test
    void deveClassificarComoNaMediaQuandoPrecoJusto() {
        // ratio = 1.00 → NA_MEDIA
        assertEquals(StatusResultado.NA_MEDIA,
                classificacaoService.classificarNegocio(100000.0, 100000.0));
    }

    @Test
    void deveClassificarComoAcimaDaMediaQuandoPrecoModeradamenteAcima() {
        // ratio = 1.15 → ACIMA_DA_MEDIA
        assertEquals(StatusResultado.ACIMA_DA_MEDIA,
                classificacaoService.classificarNegocio(115000.0, 100000.0));
    }

    @Test
    void deveClassificarComoDificilDeVenderQuandoPrecoMuitoAcima() {
        // ratio = 1.30 → DIFICIL_DE_VENDER
        assertEquals(StatusResultado.DIFICIL_DE_VENDER,
                classificacaoService.classificarNegocio(130000.0, 100000.0));
    }

    @Test
    void deveClassificarComoOtimoNegocioNaFronteiraExata() {
        // ratio = 0.90 exatamente → OTIMO_NEGOCIO (<=0.90)
        assertEquals(StatusResultado.OTIMO_NEGOCIO,
                classificacaoService.classificarNegocio(90000.0, 100000.0));
    }

    @Test
    void deveClassificarComoNaMediaNaFronteiraExata() {
        // ratio = 1.05 exatamente → NA_MEDIA (<=1.05)
        assertEquals(StatusResultado.NA_MEDIA,
                classificacaoService.classificarNegocio(105000.0, 100000.0));
    }

    @Test
    void deveClassificarComoAcimaDaMediaNaFronteiraExata() {
        // ratio = 1.20 exatamente → ACIMA_DA_MEDIA (<=1.20)
        assertEquals(StatusResultado.ACIMA_DA_MEDIA,
                classificacaoService.classificarNegocio(120000.0, 100000.0));
    }
}
