package com.glc.smartcar.avaliacoes;

import com.glc.smartcar.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.avaliacoes.dto.AvaliacaoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sc")
public class AvaliacoesController {

    private final AvaliacoesService avaliacoesService;

    public AvaliacoesController(AvaliacoesService avaliacoesService) {
        this.avaliacoesService = avaliacoesService;
    }

    @PostMapping
    public AvaliacaoResponseDTO criarAvaliacao(@Valid @RequestBody AvaliacaoRequestDTO avaliacaoRequestDTO){
        return avaliacoesService.criarAvaliacao(avaliacaoRequestDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> excluirAvaliacao(@PathVariable Long id) {
        var response = avaliacoesService.excluirAvaliacao(id);
        return ResponseEntity.ok(response);
    }
}
