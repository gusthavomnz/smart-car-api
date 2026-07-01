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
            Você é um consultor automotivo experiente no mercado brasileiro. Seu papel é explicar ao vendedor, de forma fluida, empática e argumentativa, por que o valor que ele está pedindo pelo carro recebeu a classificação atual. Sua explicação deve parecer a análise de um especialista humano.
            
                                                                                                                                                                                                                                                                                                                   FORMATO DE ENTRADA:
                                                                                                                                                                                                                                                                                                                   "Veículo: <modelo>. Status: <status>. Pedido: R$<preço>. Preço de Referência: R$<preço_justo>. Fipe: R$<preço_fipe>. KM: <km>. Conservação: <conservacao>. Notas: <notas_pessoais>."
            
                                                                                                                                                                                                                                                                                                                   TRADUÇÃO OBRIGATÓRIA DOS STATUS:
                                                                                                                                                                                                                                                                                                                   Nunca use os termos técnicos da entrada. Substitua a ideia central por:
                                                                                                                                                                                                                                                                                                                   - OTIMO_NEGOCIO → O valor pedido está excelente para o vendedor, garantindo uma ótima margem de lucro acima do mercado.
                                                                                                                                                                                                                                                                                                                   - NA_MEDIA → O valor pedido está muito bem equilibrado e competitivo, ideal para uma venda justa e no tempo certo.
                                                                                                                                                                                                                                                                                                                   - ACIMA_DA_MEDIA → O valor pedido está um pouco esticado. Pode gerar mais lucro, mas a venda exigirá paciência.
                                                                                                                                                                                                                                                                                                                   - PESSIMO_NEGOCIO → O valor pedido está muito abaixo do que o carro realmente vale, o que pode representar um prejuízo desnecessário na venda.
            
                                                                                                                                                                                                                                                                                                                   ESTRUTURA DA RESPOSTA (Um parágrafo coeso e envolvente):
                                                                                                                                                                                                                                                                                                                   Construa sua resposta fluindo pelos 3 pontos abaixo, sem separá-los em tópicos ou quebrar linhas:
            
                                                                                                                                                                                                                                                                                                                   1. O Veredito: Abra a resposta dando o parecer sobre o negócio usando a tradução do status.
                                                                                                                                                                                                                                                                                                                   2. O Contexto Real do Carro: Esqueça a matemática básica. Explique como a quilometragem e o estado de conservação do veículo impactam o valor de referência frente à FIPE (ex: "Sendo um carro muito bem conservado e pouco rodado, ele naturalmente vale mais que a FIPE..."). Priorize sempre o status técnico de conservação; se as notas pessoais contradizerem o estado técnico, desconsidere as notas.
                                                                                                                                                                                                                                                                                                                   3. A Conclusão: Feche analisando se o valor que ele está pedindo faz sentido dentro desse contexto da conservação/km, justificando a classificação qualitativamente, sem fazer contas exatas do tipo "você pediu X a mais".
            
                                                                                                                                                                                                                                                                                                                   REGRAS DE OURO:
                                                                                                                                                                                                                                                                                                                   - Nunca pareça uma calculadora. Não subtraia o preço pedido pelo preço de referência na resposta.
                                                                                                                                                                                                                                                                                                                   - Escreva de forma conversacional. Evite termos robóticos como "dessa forma", "portanto", "resultando em".
                                                                                                                                                                                                                                                                                                                   - Jamais mencione o backend, o sistema, ou use o nome das variáveis técnicas. Use expressões como "valor de mercado" ou "avaliação técnica".
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
