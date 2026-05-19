package com.glc.smartcar.integration.fipe.adapter;

import com.glc.smartcar.integration.fipe.dto.FipeNameAndCode;
import com.glc.smartcar.integration.fipe.dto.FipeVeiculoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FipeAdapterTest {

    @Mock
    FipeClient clienteFipe;

    FipeAdapter adaptadorFipe;

    @BeforeEach
    void configurar() {
        adaptadorFipe = new FipeAdapter(clienteFipe);
    }



    @Test
    void deveBuscarMarcasRepassandoAoCliente() {
        List<FipeNameAndCode> marcasEsperadas = List.of(
                new FipeNameAndCode("Toyota", "59"),
                new FipeNameAndCode("Honda", "26")
        );
        when(clienteFipe.buscarMarcas()).thenReturn(marcasEsperadas);

        List<FipeNameAndCode> resultado = adaptadorFipe.buscarMarcas();

        assertEquals(2, resultado.size());
        assertEquals("Toyota", resultado.get(0).nome());
        verify(clienteFipe).buscarMarcas();
    }

    @Test
    void deveBuscarMarcasRetornarListaVaziaQuandoNaoHouverResultados() {
        when(clienteFipe.buscarMarcas()).thenReturn(List.of());

        List<FipeNameAndCode> resultado = adaptadorFipe.buscarMarcas();

        assertEquals(0, resultado.size());
    }

    // --- buscarModelos ---

    @Test
    void deveBuscarModelosPorMarcaRepassandoAoCliente() {
        List<FipeNameAndCode> modelosEsperados = List.of(
                new FipeNameAndCode("Corolla", "5940"),
                new FipeNameAndCode("Yaris", "10591")
        );
        when(clienteFipe.buscarModelos("59")).thenReturn(modelosEsperados);

        List<FipeNameAndCode> resultado = adaptadorFipe.buscarModelos("59");

        assertEquals(2, resultado.size());
        assertEquals("Corolla", resultado.get(0).nome());
        verify(clienteFipe).buscarModelos("59");
    }


    @Test
    void deveBuscarAnosRepassandoAoCliente() {
        List<FipeNameAndCode> anosEsperados = List.of(
                new FipeNameAndCode("2020 Gasolina", "2020-1"),
                new FipeNameAndCode("2019 Gasolina", "2019-1")
        );
        when(clienteFipe.buscarAnos("59", "5940")).thenReturn(anosEsperados);

        List<FipeNameAndCode> resultado = adaptadorFipe.buscarAnos("59", "5940");

        assertEquals(2, resultado.size());
        assertEquals("2020-1", resultado.get(0).codigo());
        verify(clienteFipe).buscarAnos("59", "5940");
    }


    @Test
    void deveObterPrecoRepassandoAoCliente() {
        FipeVeiculoDTO veiculoEsperado = new FipeVeiculoDTO(
                "R$ 48.000,00", "Toyota", "Corolla", 2020,
                "Gasolina", "001234-5", "Maio 2024"
        );
        when(clienteFipe.obterPreco("59", "5940", "2020-1")).thenReturn(veiculoEsperado);

        FipeVeiculoDTO resultado = adaptadorFipe.obterPreco("59", "5940", "2020-1");

        assertNotNull(resultado);
        assertEquals("R$ 48.000,00", resultado.Valor());
        assertEquals("001234-5", resultado.CodigoFipe());
        verify(clienteFipe).obterPreco("59", "5940", "2020-1");
    }

    @Test
    void devePassarParametrosCorretosAoObterPreco() {
        FipeVeiculoDTO veiculoEsperado = new FipeVeiculoDTO(
                "R$ 90.000,00", "Honda", "Civic", 2022,
                "Gasolina", "002345-6", "Junho 2024"
        );
        when(clienteFipe.obterPreco("26", "6780", "2022-1")).thenReturn(veiculoEsperado);

        adaptadorFipe.obterPreco("26", "6780", "2022-1");

        verify(clienteFipe).obterPreco("26", "6780", "2022-1");
    }
}
