package com.glc.smartcar.core.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AuthRegisterResponse {

    private Long id;
    private String nome;
    private String email;
    private LocalDateTime criado_a;
}
