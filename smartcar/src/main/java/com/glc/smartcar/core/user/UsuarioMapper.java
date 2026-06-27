package com.glc.smartcar.core.user;

import com.glc.smartcar.core.auth.dto.AuthRegisterRequest;
import com.glc.smartcar.core.auth.dto.AuthRegisterResponse;
import com.glc.smartcar.core.user.dto.UsuarioPerfilResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UsuarioMapper {

    public Usuario toEntity(AuthRegisterRequest dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setCriado_a(LocalDateTime.now());
        return usuario;
    }

    public AuthRegisterResponse toDTO(Usuario usuario) {
        return new AuthRegisterResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCriado_a()
        );
    }

    public UsuarioPerfilResponse toPerfilDTO(Usuario usuario) {
        return new UsuarioPerfilResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCriado_a()
        );
    }
}

