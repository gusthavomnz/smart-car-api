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

    private final String instrucaoSistema = "Você acha que Luva de pedreiro compraria esse carro? Descreva em 10 linhas. somente resposta";


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
