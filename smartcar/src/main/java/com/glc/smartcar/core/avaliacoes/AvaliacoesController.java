package com.glc.smartcar.core.avaliacoes;

import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoResponseDTO;
import com.glc.smartcar.core.user.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sc")
public class AvaliacoesController {

    private final AvaliacoesService avaliacoesService;

    public AvaliacoesController(AvaliacoesService avaliacoesService) {
        this.avaliacoesService = avaliacoesService;
    }

    @PostMapping
    public ResponseEntity<AvaliacaoResponseDTO> criarAvaliacao(@Valid @RequestBody AvaliacaoRequestDTO dto,
                                                               @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(avaliacoesService.criarAvaliacao(dto, usuario.getId()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> excluirAvaliacao(@PathVariable Long id,
                                                                 @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(avaliacoesService.excluirAvaliacao(id, usuario.getId()));
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarAvaliacoes(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(avaliacoesService.listarAvaliacoesPorUsuario(usuario.getId()));
    }

}
