package com.glc.smartcar.core.user;

import com.glc.smartcar.core.user.dto.CadastroRequestDTO;
import com.glc.smartcar.core.user.dto.LoginRequestDTO;
import com.glc.smartcar.core.user.dto.LoginResponseDTO;
import com.glc.smartcar.core.user.dto.UsuarioResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UsuarioService {

    private final UserRepository userRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UserRepository repositorioUsuario, UsuarioMapper mapeador) {
        this.userRepository = repositorioUsuario;
        this.usuarioMapper = mapeador;
    }

    public UsuarioResponseDTO cadastrar(CadastroRequestDTO dto) {
        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setSenha(dto.getSenha()); // Implementar encriptgrafia de senha aqui depois.
        return usuarioMapper.toDTO(userRepository.save(usuario));
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        // validar autenticidade e logar
        return new LoginResponseDTO("sem_valor_ainda");
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return usuarioMapper.toDTO(usuario);
    }
}
