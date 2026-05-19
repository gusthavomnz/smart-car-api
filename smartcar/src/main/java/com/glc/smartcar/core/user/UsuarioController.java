package com.glc.smartcar.core.user;

import com.glc.smartcar.core.user.dto.CadastroRequestDTO;
import com.glc.smartcar.core.user.dto.LoginRequestDTO;
import com.glc.smartcar.core.user.dto.LoginResponseDTO;
import com.glc.smartcar.core.user.dto.UsuarioResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService servicoUsuario) {
        this.usuarioService = servicoUsuario;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody CadastroRequestDTO dto) {
        return ResponseEntity.status(201).body(usuarioService.cadastrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.login(dto));
    }

}
