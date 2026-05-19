package com.glc.smartcar.core.user;

import com.glc.smartcar.core.user.dto.CadastroRequestDTO;
import com.glc.smartcar.core.user.dto.LoginResponseDTO;
import com.glc.smartcar.core.user.dto.UsuarioResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock UserRepository userRepository;
    @Mock UsuarioMapper usuarioMapper;

    @InjectMocks
    UsuarioService usuarioService;

    private CadastroRequestDTO criarDtoCadastro() {
        CadastroRequestDTO dto = new CadastroRequestDTO();
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
        usuario.setSenha("senha123");
        usuario.setCriado_a(LocalDateTime.of(2024, 1, 15, 10, 0));
        return usuario;
    }




    // REFATORAR QUANDO IMPLEMENTAR SECURITY
    @Test
    void deveCadastrarUsuarioComSucesso() {
        Usuario usuarioSalvo = criarUsuario();
        UsuarioResponseDTO respostaEsperada = new UsuarioResponseDTO(1L, "João Silva", "joao@email.com", usuarioSalvo.getCriado_a());

        when(userRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);
        when(usuarioMapper.toDTO(usuarioSalvo)).thenReturn(respostaEsperada);

        UsuarioResponseDTO resultado = usuarioService.cadastrar(criarDtoCadastro());

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao@email.com", resultado.getEmail());
        verify(userRepository).save(any(Usuario.class));
    }

    @Test
    void deveSalvarSenhaDoDto() {
        Usuario usuarioSalvo = criarUsuario();
        when(userRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);
        when(usuarioMapper.toDTO(any())).thenReturn(new UsuarioResponseDTO(1L, "João Silva", "joao@email.com", LocalDateTime.now()));

        usuarioService.cadastrar(criarDtoCadastro());

        verify(userRepository).save(argThat(u -> u.getSenha().equals("senha123")));
    }

    // --- login ---

    @Test
    void deveRetornarTokenAoFazerLogin() {
        LoginResponseDTO resultado = usuarioService.login(null);

        assertNotNull(resultado);
        assertNotNull(resultado.getToken());
    }

    // --- buscarPorId ---

    @Test
    void deveBuscarUsuarioPorIdComSucesso() {
        Usuario usuario = criarUsuario();
        UsuarioResponseDTO respostaEsperada = new UsuarioResponseDTO(1L, "João Silva", "joao@email.com", usuario.getCriado_a());

        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(respostaEsperada);

        UsuarioResponseDTO resultado = usuarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("João Silva", resultado.getNome());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> usuarioService.buscarPorId(99L));
    }
}
