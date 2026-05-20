package com.glc.smartcar.integration.ia.adapter;

import com.glc.smartcar.integration.ia.dto.Message;
import com.glc.smartcar.integration.ia.dto.iaRequest;
import com.glc.smartcar.integration.ia.dto.iaResponse;
import com.glc.smartcar.integration.ia.port.IaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IaAdapter implements IaPort {

    private final IaClient iaClient;
    private final String apiKey;
    private final String modeloUtilizado = "llama-3.1-8b-instant";
    private final double temperaturaUtilizada = 0.7;

    public IaAdapter(IaClient iaClient, @Value("${API_KEY}") String apiKey) {
        this.iaClient = iaClient;
        this.apiKey = apiKey;
    }

    private final String instrucaoSistema = """
            Você é um assistente especializado em veículos usados no Brasil. \
            Um sistema já analisou o anúncio e chegou a uma classificação final. Sua única função é explicar \
            essa classificação ao comprador em linguagem simples e direta. \
            O sistema calcula um preço justo baseado no preço da tabela FIPE, ajustado pela quilometragem \
            (veículos com mais km que a média para o ano são penalizados, com menos km recebem bônus) \
            e pelo estado de conservação do veículo. O preço anunciado é então comparado ao preço justo: \
            até 90% do justo é ótimo negócio, 90-105% está na média, 105-120% está acima da média, \
            acima de 120% é difícil de vender. \
            Explique em até 4 linhas POR QUE o anúncio recebeu essa classificação com base nesses critérios. \
            Nunca questione ou reavalie o resultado. Nunca use saudações ou introduções. \
            Responda sempre em português brasileiro.\
            """;


    @Override
    public List<Message> criarContexto(String dadosUsuario) {
        return List.of(
                new Message("system", instrucaoSistema),
                new Message("user", dadosUsuario)
        );
    }

    @Override
    public String executarRequisicaoIA(List<Message> mensagens) {
        iaRequest request = new iaRequest(modeloUtilizado, mensagens, temperaturaUtilizada);
        try {
            iaResponse response = iaClient.enviarPrompt("Bearer " + apiKey, request);
            if (response != null && response.choices() != null && !response.choices().isEmpty()) {
                return response.choices().get(0).message().content();
            }
            return "A IA não retornou dados.";
        } catch (Exception e) {
            return "Erro na integração: " + e.getMessage();
        }
    }
}
