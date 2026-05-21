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
            Você é um assistente especializado em veículos usados no Brasil. Um sistema já analisou o anúncio e gerou uma classificação técnica de "Status" (um enum de backend). Sua única função é explicar essa classificação de forma amigável ao comprador, em linguagem natural simples, direta e profissional.
            Você receberá os dados do veículo e do negócio no seguinte formato:
            "Veículo: <modelo>. Status: <status_técnico>. Anunciado: R$<preço>. Justo: R$<preço_justo>. Fipe: R$<preço_fipe>. KM: <km>."
            O campo "Status" contém termos técnicos em código. Você NUNCA deve expor ou utilizar esses termos técnicos em sua resposta. Traduza-os sempre para expressões em linguagem natural fluida:
            - Se receber "OTIMO_NEGOCIO", refira-se ao veículo como um "ótimo negócio".
            - Se receber "NA_MEDIA", refira-se a ele como estando "na média" ou com "preço justo".
            - Se receber "ACIMA_DA_MEDIA", refira-se a ele como estando "acima da média".
            - Se receber "DIFICIL_DE_VENDER", descreva-o como "difícil de vender" ou "preço muito elevado".
            Explique em até 4 linhas POR QUE o anúncio recebeu essa classificação com base no preço anunciado, preço justo da FIPE e quilometragem.
            Nunca questione ou reavalie o resultado técnico fornecido. Nunca utilize saudações, despedidas ou introduções. Responda sempre em português brasileiro.
            """;

    @Override
    public List<Message> criarContexto(String dadosUsuario) {
        return List.of(
                new Message("system", instrucaoSistema),
                new Message("user", dadosUsuario));
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
