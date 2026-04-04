package com.glc.smartcar.fipe.dto;

import java.util.List;

public record FipeModelosResponse(
        List<FipeInfoRequest> modelos,
        List<FipeInfoRequest> anos
) {}