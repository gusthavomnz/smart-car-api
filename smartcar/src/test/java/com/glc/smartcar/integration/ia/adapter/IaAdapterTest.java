package com.glc.smartcar.integration.ia.adapter;

import com.glc.smartcar.integration.ia.dto.Choice;
import com.glc.smartcar.integration.ia.dto.Message;
import com.glc.smartcar.integration.ia.dto.iaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IaAdapterTest {

    @Mock
    IaClient clienteIA;

    IaAdapter adaptadorIA;

    @BeforeEach
    void configurar() {
        adaptadorIA = new IaAdapter(clienteIA, "chave-teste-123");
    }

    // --- criarContexto ---

    @Test
    void deveCriarContextoComDuasMensagens() {
        List<Message> mensagens = adaptadorIA.criarContexto("dados do carro");

        assertEquals(2, mensagens.size());
    }

    @Test
    void deveCriarContextoComPrimeiraMensagemDoSistema() {
        List<Message> mensagens = adaptadorIA.criarContexto("dados do carro");

        assertEquals("system", mensagens.get(0).role());
        assertThat(mensagens.get(0).content()).isNotBlank();
    }

    @Test
    void deveCriarContextoComSegundaMensagemDoUsuario() {
        List<Message> mensagens = adaptadorIA.criarContexto("dados do carro teste");

        assertEquals("user", mensagens.get(1).role());
        assertEquals("dados do carro teste", mensagens.get(1).content());
    }

    // --- executarRequisicaoIA ---

    @Test
    void deveRetornarConteudoDaRespostaIA() {
        Message mensagemResposta = new Message("assistant", "Luva de pedreiro compraria esse carro sim!");
        Choice escolha = new Choice(mensagemResposta);
        iaResponse resposta = new iaResponse(List.of(escolha));

        when(clienteIA.enviarPrompt(anyString(), any())).thenReturn(resposta);

        String resultado = adaptadorIA.executarRequisicaoIA(List.of());

        assertEquals("Luva de pedreiro compraria esse carro sim!", resultado);
    }

    @Test
    void deveRetornarMensagemPadraoQuandoRespostaForNula() {
        when(clienteIA.enviarPrompt(anyString(), any())).thenReturn(null);

        String resultado = adaptadorIA.executarRequisicaoIA(List.of());

        assertEquals("A IA não retornou dados.", resultado);
    }

    @Test
    void deveRetornarMensagemPadraoQuandoListaDeEscolhasVazia() {
        iaResponse respostaVazia = new iaResponse(List.of());
        when(clienteIA.enviarPrompt(anyString(), any())).thenReturn(respostaVazia);

        String resultado = adaptadorIA.executarRequisicaoIA(List.of());

        assertEquals("A IA não retornou dados.", resultado);
    }

    @Test
    void deveRetornarMensagemDeErroQuandoExcecaoLancada() {
        when(clienteIA.enviarPrompt(anyString(), any())).thenThrow(new RuntimeException("conexão recusada"));

        String resultado = adaptadorIA.executarRequisicaoIA(List.of());

        assertThat(resultado).startsWith("Erro na integração:");
        assertThat(resultado).contains("conexão recusada");
    }

    @Test
    void devePassarChaveDeAutorizacaoNaRequisicao() {
        Message mensagemResposta = new Message("assistant", "Resposta da IA");
        iaResponse resposta = new iaResponse(List.of(new Choice(mensagemResposta)));

        when(clienteIA.enviarPrompt(anyString(), any())).thenReturn(resposta);

        adaptadorIA.executarRequisicaoIA(List.of());

        org.mockito.Mockito.verify(clienteIA).enviarPrompt(
                org.mockito.ArgumentMatchers.startsWith("Bearer "),
                any()
        );
    }
}
