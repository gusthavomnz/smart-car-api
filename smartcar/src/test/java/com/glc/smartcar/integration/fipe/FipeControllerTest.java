package com.glc.smartcar.integration.fipe;

import com.glc.smartcar.integration.fipe.dto.FipeNameAndCode;
import com.glc.smartcar.integration.fipe.dto.FipeVeiculoDTO;
import com.glc.smartcar.integration.fipe.port.FipePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FipeControllerTest {

    @Mock
    FipePort porteFipe;

    @InjectMocks
    FipeController controladorFipe;

    // --- buscarMarcas ---

    @Test
    void deveBuscarTodasAsMarcas() {
        List<FipeNameAndCode> marcasEsperadas = List.of(
                new FipeNameAndCode("Toyota", "59"),
                new FipeNameAndCode("Honda", "26")
        );
        when(porteFipe.buscarMarcas()).thenReturn(marcasEsperadas);

        List<FipeNameAndCode> resultado = controladorFipe.buscarMarcas();

        assertEquals(2, resultado.size());
        assertEquals("Toyota", resultado.get(0).nome());
        verify(porteFipe).buscarMarcas();
    }

    @Test
    void deveBuscarMarcasRetornarListaVazia() {
        when(porteFipe.buscarMarcas()).thenReturn(List.of());

        List<FipeNameAndCode> resultado = controladorFipe.buscarMarcas();

        assertEquals(0, resultado.size());
    }

    // --- buscarModelos ---

    @Test
    void deveBuscarModelosPorMarca() {
        List<FipeNameAndCode> modelosEsperados = List.of(
                new FipeNameAndCode("Corolla", "5940")
        );
        when(porteFipe.buscarModelos("59")).thenReturn(modelosEsperados);

        List<FipeNameAndCode> resultado = controladorFipe.buscarModelos("59");

        assertEquals(1, resultado.size());
        assertEquals("Corolla", resultado.get(0).nome());
        verify(porteFipe).buscarModelos("59");
    }

    // --- buscarAnoGasolina ---

    @Test
    void deveBuscarAnosPorMarcaEModelo() {
        List<FipeNameAndCode> anosEsperados = List.of(
                new FipeNameAndCode("2020 Gasolina", "2020-1"),
                new FipeNameAndCode("2019 Gasolina", "2019-1")
        );
        when(porteFipe.buscarAnos("59", "5940")).thenReturn(anosEsperados);

        List<FipeNameAndCode> resultado = controladorFipe.buscarAnoGasolina("59", "5940");

        assertEquals(2, resultado.size());
        assertEquals("2020-1", resultado.get(0).codigo());
        verify(porteFipe).buscarAnos("59", "5940");
    }

    // --- buscarVeiculoFipe ---

    @Test
    void deveBuscarVeiculoComPrecoFipe() {
        FipeVeiculoDTO veiculoEsperado = new FipeVeiculoDTO(
                "R$ 48.000,00", "Toyota", "Corolla", 2020,
                "Gasolina", "001234-5", "Maio 2024"
        );
        when(porteFipe.obterPreco("59", "5940", "2020-1")).thenReturn(veiculoEsperado);

        FipeVeiculoDTO resultado = controladorFipe.buscarVeiculoFipe("59", "5940", "2020-1");

        assertNotNull(resultado);
        assertEquals("R$ 48.000,00", resultado.Valor());
        assertEquals("Toyota", resultado.Marca());
        assertEquals("Corolla", resultado.Modelo());
        assertEquals("001234-5", resultado.CodigoFipe());
        verify(porteFipe).obterPreco("59", "5940", "2020-1");
    }

    @Test
    void deveDelegarBuscaDeVeiculoAoPorteCorreto() {
        FipeVeiculoDTO veiculoEsperado = new FipeVeiculoDTO(
                "R$ 90.000,00", "Honda", "Civic", 2022,
                "Gasolina", "002345-6", "Junho 2024"
        );
        when(porteFipe.obterPreco("26", "6780", "2022-1")).thenReturn(veiculoEsperado);

        controladorFipe.buscarVeiculoFipe("26", "6780", "2022-1");

        verify(porteFipe).obterPreco("26", "6780", "2022-1");
    }
}
