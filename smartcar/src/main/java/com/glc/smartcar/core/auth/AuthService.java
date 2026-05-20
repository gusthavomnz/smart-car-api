package com.glc.smartcar.core.auth;

import com.glc.smartcar.config.TokenProvider;
import com.glc.smartcar.core.auth.dto.AuthLoginRequest;
import com.glc.smartcar.core.auth.dto.AuthLoginResponse;
import com.glc.smartcar.core.auth.dto.AuthRegisterRequest;
import com.glc.smartcar.core.auth.dto.AuthRegisterResponse;
import com.glc.smartcar.core.user.UserRepository;
import com.glc.smartcar.core.user.Usuario;
import com.glc.smartcar.core.user.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UsuarioMapper usuarioMapper;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public AuthRegisterResponse cadastrar(AuthRegisterRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        dto.setSenha(passwordEncoder.encode(dto.getSenha()));
        Usuario usuario = usuarioMapper.toEntity(dto);
        return usuarioMapper.toDTO(userRepository.save(usuario));
    }

    public AuthLoginResponse login(AuthLoginRequest dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
        );
        String token = tokenProvider.gerarToken(authentication);
        return new AuthLoginResponse(token);
    }


}
