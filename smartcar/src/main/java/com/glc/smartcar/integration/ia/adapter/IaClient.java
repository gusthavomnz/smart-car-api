package com.glc.smartcar.integration.ia.adapter;

import com.glc.smartcar.integration.ia.dto.iaRequest;
import com.glc.smartcar.integration.ia.dto.iaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "groq-api", url = "https://api.groq.com/openai/v1")
public interface IaClient {

    @PostMapping("/chat/completions")
    iaResponse enviarPrompt(
            @RequestHeader("Authorization") String auth,
            @RequestBody iaRequest request
    );
}
