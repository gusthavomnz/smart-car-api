package com.glc.smartcar.core.user;


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




}
