package com.glc.smartcar.fipe.dto;

import java.util.List;

public record FipeModelosResponse(
        List<FipeMarcaDTO> modelos,
        List<FipeMarcaDTO> anos
) {}