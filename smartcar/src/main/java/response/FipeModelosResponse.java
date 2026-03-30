package response;

import request.FipeInfoRequest;

import java.util.List;

public record FipeModelosResponse(
        List<FipeInfoRequest> modelos,
        List<FipeInfoRequest> anos
) {}