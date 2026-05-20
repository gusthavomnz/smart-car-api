package com.glc.smartcar.core.user;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock UserRepository userRepository;
    @Mock UsuarioMapper usuarioMapper;

    @InjectMocks
    UsuarioService usuarioService;
}
