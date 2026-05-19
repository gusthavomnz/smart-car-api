package com.glc.smartcar.core.user;

import com.glc.smartcar.core.user.dto.CadastroRequestDTO;
import com.glc.smartcar.core.user.dto.UsuarioResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UsuarioMapper {

    public Usuario toEntity(CadastroRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setCriado_a(LocalDateTime.now());
        return usuario;
    }

    public UsuarioResponseDTO toDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCriado_a()
        );
    }
}
