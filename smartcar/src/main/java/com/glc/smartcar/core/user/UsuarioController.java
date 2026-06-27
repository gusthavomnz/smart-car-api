package com.glc.smartcar.core.user;


import com.glc.smartcar.core.user.dto.AlterarSenhaRequest;
import com.glc.smartcar.core.user.dto.UsuarioPerfilResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioPerfilResponse> buscarPerfil(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(usuarioService.buscarPerfil(usuario.getEmail()));
    }

    @PutMapping("/senha")
    public ResponseEntity<Void> alterarSenha(@AuthenticationPrincipal Usuario usuario,
                                              @RequestBody @Valid AlterarSenhaRequest dto) {
        usuarioService.alterarSenha(usuario.getEmail(), dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> excluirConta(@AuthenticationPrincipal Usuario usuario) {
        usuarioService.excluirConta(usuario.getEmail());
        return ResponseEntity.noContent().build();
    }
}
