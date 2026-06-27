package com.glc.smartcar.core.user;


import com.glc.smartcar.core.avaliacoes.AvaliacoesRepository;
import com.glc.smartcar.core.user.dto.AlterarSenhaRequest;
import com.glc.smartcar.core.user.dto.UsuarioPerfilResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
public class git UsuarioService {

    private final UserRepository userRepository;
    private final UsuarioMapper usuarioMapper;
    private final AvaliacoesRepository avaliacoesRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UserRepository userRepository, UsuarioMapper usuarioMapper,
                          AvaliacoesRepository avaliacoesRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.usuarioMapper = usuarioMapper;
        this.avaliacoesRepository = avaliacoesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioPerfilResponse buscarPerfil(String email) {
        Usuario usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return usuarioMapper.toPerfilDTO(usuario);
    }

    public void alterarSenha(String email, AlterarSenhaRequest dto) {
        Usuario usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        userRepository.save(usuario);
    }

    @Transactional
    public void excluirConta(String email) {
        Usuario usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        avaliacoesRepository.deleteAllByUsuarioId(usuario.getId());
        userRepository.delete(usuario);
    }
}
