package com.glc.smartcar.core.avaliacoes;

import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.core.avaliacoes.dto.AvaliacaoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    // Substituir AvaliacaoResponseDTO para String para testes
    public String criarAvaliacao(@Valid @RequestBody AvaliacaoRequestDTO avaliacaoRequestDTO){
        return avaliacoesService.criarAvaliacao(avaliacaoRequestDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> excluirAvaliacao(@PathVariable Long id) {
        var response = avaliacoesService.excluirAvaliacao(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarAvaliacoes(@PathVariable Long usuarioId) {
        // Quando implementar SpringSecurity, não receber usuarioID no parametro e obter usuarioID via TokenJWT
        List<AvaliacaoResponseDTO> lista = avaliacoesService.listarAvaliacoesPorUsuario(usuarioId);
        return ResponseEntity.ok(lista);
    }

}
