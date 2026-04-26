package com.glc.smartcar.avaliacoes;

import com.glc.smartcar.avaliacoes.dto.AvaliacaoRequestDTO;
import com.glc.smartcar.avaliacoes.dto.AvaliacaoResponseDTO;
import jakarta.validation.Valid;
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
}
