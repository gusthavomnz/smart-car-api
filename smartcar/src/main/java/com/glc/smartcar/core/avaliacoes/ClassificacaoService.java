package com.glc.smartcar.core.avaliacoes;

import com.glc.smartcar.core.avaliacoes.enums.Conservacao;
import com.glc.smartcar.core.avaliacoes.enums.StatusResultado;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClassificacaoService {

    // -> Usei ~14.000 km/ano como referência de uso típico, baseado em estimativas comuns do mercado automotivo brasileiro.
    private static final double KM_MEDIO_ANUAL = 14_000.0;

    /* Controla o impacto da quilometragem no preço (na função exponencial).
    Diferença de 10.000 km → impacto leve
    Diferença de 100.000 km → impacto significativo*/
    private static final double COEF_DECAIMENTO_KM = 0.00002;


    /* Defini limites para evitar distorções irreais, garantindo que a quilometragem,
     não reduza ou aumente o preço além de valores plausíveis.
     Pior caso (muito rodado) → -40% no preço // Melhor caso (pouco rodado) → +15% */
    private static final double FATOR_KM_MIN = 0.60;
    private static final double FATOR_KM_MAX = 1.15;


    /* Ponto neutro da conservação.
       Modelei a conservação como uma escala de 0 a 1, onde 0.5 representa um estado médio.
     */
    private static final double CONS_NEUTRO = 0.5;

    /* Quanto a conservação impacta o preço.
    Defini um peso moderado para refletir que o estado do veículo influencia o preço, mas não mais que fatores estruturais como quilometragem.”
Conservação 1.0 → +20%
Conservação 0.0 → -20%
     */
    private static final double PESO_CONSERVACAO = 0.4;


    /* Limites do impacto da conservação.Apliquei limites para evitar que avaliações subjetivas de conservação gerem distorções exageradas no preço.
carro ruim → até -20%
carro impecável → até +20%
     */
    private static final double FATOR_CONS_MIN = 0.80;
    private static final double FATOR_CONS_MAX = 1.20;

    double parseFipe(String valorFipe) {
        return Double.parseDouble(
                valorFipe
                        .replace("R$ ", "")
                        .replace(".", "")
                        .replace(",", ".")
        );
    }

    //Converte o YearId para Year normal. A tabela fipe retorna algo como: 2025-1 2026-1 e precisamos apenas do inicio.
    int parseYearId(String yearId) {
        return Integer.parseInt(yearId.split("-")[0]);
    }

    /* Idade do veículo em anos completos. */
    int calcularIdade(int anoFabricacao, int anoAtual) {
        return Math.max(0, anoAtual - anoFabricacao);
    }

    /* KM esperado para a idade do veículo. */
    double calcularKmEsperado(int idadeAnos) {
        return idadeAnos * KM_MEDIO_ANUAL;
    }

    /**
     * Fator de ajuste por quilometragem (decaimento exponencial).
     * Positivo → rodou mais → penalidade. Negativo → rodou menos → bônus.
     */
    double calcularFatorKm(double kmAtual, double kmEsperado) {
        double diferenca = kmAtual - kmEsperado;
        double fator = Math.exp(-COEF_DECAIMENTO_KM * diferenca);
        return clamp(fator, FATOR_KM_MIN, FATOR_KM_MAX);
    }

    double calcularFatorConservacao(Conservacao conservacao) {
        double valorConservacao = conservacao.getValor();
        double fator = 1.0 + PESO_CONSERVACAO * (valorConservacao - CONS_NEUTRO);
        return clamp(fator, FATOR_CONS_MIN, FATOR_CONS_MAX);
    }

    private double clamp(double valor, double min, double max) {
        return Math.max(min, Math.min(max, valor));
    }

    double calcularPrecoJusto(double precoFipe, int anoFabricacao, double kmAtual, Conservacao conservacao) {
        int idade = calcularIdade(anoFabricacao, LocalDateTime.now().getYear());
        double kmEsper = calcularKmEsperado(idade);
        double fatorKm = calcularFatorKm(kmAtual, kmEsper);
        double fatorCons = calcularFatorConservacao(conservacao);
        return precoFipe * fatorKm * fatorCons;
    }

    StatusResultado classificarNegocio(double precoAnunciado, double precoJusto) {
        double indice = precoAnunciado / precoJusto;

        if (indice <= 0.90) return StatusResultado.OTIMO_NEGOCIO;
        if (indice <= 1.05) return StatusResultado.NA_MEDIA;
        if (indice <= 1.20) return StatusResultado.ACIMA_DA_MEDIA;
        return StatusResultado.PESSIMO_NEGOCIO;
    }

}
