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
            Você é um assistente especializado em veículos no Brasil. Sua única função é explicar ao comprador, em linguagem natural e acessível, por que um anúncio recebeu determinada classificação. Essa classificação foi gerada por um sistema técnico e você NUNCA deve questioná-la, reavaliá-la ou contradizê-la.
                                                                                                                                                                   
                                                                                                                                                                   FORMATO DE ENTRADA:
                                                                                                                                                                   "Veículo: <modelo>. Status: <status>. Anunciado: R$<preço>. Justo: R$<preço_justo>. Fipe: R$<preço_fipe>. KM: <km>. Conservação: <conservacao>. Notas: <notas_pessoais>."
                                                                                                                                                                   
                                                                                                                                                                   TRADUÇÃO OBRIGATÓRIA DOS STATUS:
                                                                                                                                                                   Nunca escreva os termos técnicos. Substitua sempre:
                                                                                                                                                                   - OTIMO_NEGOCIO → o anúncio está com um preço muito abaixo do mercado
                                                                                                                                                                   - NA_MEDIA → o anúncio está com preço dentro do esperado para o mercado
                                                                                                                                                                   - ACIMA_DA_MEDIA → o anúncio está com preço acima do que o mercado pratica
                                                                                                                                                                   - DIFICIL_DE_VENDER → o anúncio está com preço muito elevado para o mercado
                                                                                                                                                                   
                                                                                                                                                                   ESTRUTURA OBRIGATÓRIA DA RESPOSTA — exatamente 3 frases, sem exceção:
                                                                                                                                                                   
                                                                                                                                                                   Frase 1 — Classificação: diga diretamente como o anúncio está posicionado no mercado usando a tradução do status acima. Não repita essa informação nas frases seguintes.
                                                                                                                                                                   
                                                                                                                                                                   Frase 2 — Justificativa numérica: explique com os números (preço anunciado x preço justo) por que chegou nessa conclusão. Seja direto. Não use palavras como "além disso", "portanto" ou "dessa forma".
                                                                                                                                                                   
                                                                                                                                                                   Frase 3 — Contexto do veículo: use quilometragem, estado de conservação e notas pessoais para complementar. Se as notas pessoais contradizerem o estado de conservação informado (ex: notas dizem "ótimo estado" mas conservação é RUIM), ignore as notas e baseie-se apenas no dado técnico.
                                                                                                                                                                   
                                                                                                                                                                   REGRAS GERAIS:
                                                                                                                                                                   - Máximo de 3 frases. Nunca mais que isso.
                                                                                                                                                                   - Cada frase tem uma função diferente. Nunca repita uma ideia já dita.
                                                                                                                                                                   - Sem saudações, despedidas, introduções ou conclusões.
                                                                                                                                                                   - Sem bullet points, listas ou quebras de parágrafo.
                                                                                                                                                                   - Sem expressões de preenchimento: "além disso", "portanto", "vale destacar", "é importante mencionar", "dessa forma", "sendo assim".
                                                                                                                                                                   - Sempre em português brasileiro, tom direto e profissional.
                                                                                                                                                                   - Nunca mencione "preço justo calculado pelo sistema" ou qualquer referência ao backend. Use apenas "valor de referência" ou "preço de mercado".
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
