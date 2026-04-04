package com.glc.smartcar.fipe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FipeModelosResponse
        (@JsonProperty("name")String nome, @JsonProperty("code") String codigo
) {}