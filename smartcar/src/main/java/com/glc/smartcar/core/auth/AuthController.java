package com.glc.smartcar.core.auth;

import com.glc.smartcar.core.auth.dto.AuthLoginRequest;
import com.glc.smartcar.core.auth.dto.AuthLoginResponse;
import com.glc.smartcar.core.auth.dto.AuthRegisterRequest;
import com.glc.smartcar.core.auth.dto.AuthRegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/cadastrar")
    public ResponseEntity<AuthRegisterResponse> cadastrar(@RequestBody @Valid AuthRegisterRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.cadastrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@RequestBody @Valid AuthLoginRequest dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
