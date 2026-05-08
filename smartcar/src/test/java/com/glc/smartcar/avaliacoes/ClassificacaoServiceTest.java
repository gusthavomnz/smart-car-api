package com.glc.smartcar.avaliacoes;

import com.glc.smartcar.core.avaliacoes.ClassificacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ClassificacaoServiceTest {


    @InjectMocks
    ClassificacaoService classificacaoService;

    @Test
    void deveRetornarIdadeDoCarro() {
        int anoFabricacao = 2024;
        int anoSimulacao = 2026;

        int idadeCarro = classificacaoService.calcularIdade(anoFabricacao, anoSimulacao);
        assertEquals(2, idadeCarro, "A idade retornada deve ser 2");
    }

    @Test
    void deveRetornarZeroParaCarrosFabricadosAnoSeguinte() {
        int anoFabricacao = 2027;
        int anoSimulacao = 2026;
        int idade = classificacaoService.calcularIdade(anoFabricacao,anoSimulacao);
        assertEquals(0,idade,"A idade retornada deve ser 0. Não pode retornar negativo.");
    }

}