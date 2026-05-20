package com.glc.smartcar.core.auth;

import com.glc.smartcar.config.TokenProvider;
import com.glc.smartcar.core.auth.dto.AuthLoginRequest;
import com.glc.smartcar.core.auth.dto.AuthLoginResponse;
import com.glc.smartcar.core.auth.dto.AuthRegisterRequest;
import com.glc.smartcar.core.auth.dto.AuthRegisterResponse;
import com.glc.smartcar.core.user.UserRepository;
import com.glc.smartcar.core.user.Usuario;
import com.glc.smartcar.core.user.UsuarioMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock UsuarioMapper usuarioMapper;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock TokenProvider tokenProvider;

    @InjectMocks
    AuthService authService;

    private AuthRegisterRequest criarRegisterRequest() {
        AuthRegisterRequest dto = new AuthRegisterRequest();
        dto.setNome("João Silva");
        dto.setEmail("joao@email.com");
        dto.setSenha("senha123");
        return dto;
    }

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senha_encriptografada");
        usuario.setCriado_a(LocalDateTime.of(2024, 1, 15, 10, 0));
        return usuario;
    }

    // --- cadastrar ---

    @Test
    void deveCadastrarUsuarioComSucesso() {
        Usuario usuarioSalvo = criarUsuario();
        AuthRegisterResponse respostaEsperada = new AuthRegisterResponse(1L, "João Silva", "joao@email.com", usuarioSalvo.getCriado_a());

        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("senha_encriptografada");
        when(usuarioMapper.toEntity(any(AuthRegisterRequest.class))).thenReturn(usuarioSalvo);
        when(userRepository.save(usuarioSalvo)).thenReturn(usuarioSalvo);
        when(usuarioMapper.toDTO(usuarioSalvo)).thenReturn(respostaEsperada);

        AuthRegisterResponse resultado = authService.cadastrar(criarRegisterRequest());

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao@email.com", resultado.getEmail());
        verify(userRepository).save(usuarioSalvo);
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.cadastrar(criarRegisterRequest()));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deveEncriptografarSenhaAntesDeSalvar() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("senha_encriptografada");
        when(usuarioMapper.toEntity(any())).thenReturn(criarUsuario());
        when(userRepository.save(any())).thenReturn(criarUsuario());
        when(usuarioMapper.toDTO(any())).thenReturn(new AuthRegisterResponse(1L, "João Silva", "joao@email.com", LocalDateTime.now()));

        authService.cadastrar(criarRegisterRequest());

        verify(passwordEncoder).encode("senha123");
    }

    // --- login ---

    @Test
    void deveRetornarTokenAoFazerLogin() {
        AuthLoginRequest loginRequest = new AuthLoginRequest();
        loginRequest.setEmail("joao@email.com");
        loginRequest.setSenha("senha123");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.gerarToken(authentication)).thenReturn("jwt-token");

        AuthLoginResponse resultado = authService.login(loginRequest);

        assertNotNull(resultado);
        assertEquals("jwt-token", resultado.getToken());
    }
}
