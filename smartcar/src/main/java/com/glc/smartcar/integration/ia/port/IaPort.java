package com.glc.smartcar.integration.ia.port;

import com.glc.smartcar.integration.ia.dto.Message;

import java.util.List;

public interface IaPort {
    List<Message> criarContexto(String dadosUsuario);
    String executarRequisicaoIA(List<Message> mensagens);
}
