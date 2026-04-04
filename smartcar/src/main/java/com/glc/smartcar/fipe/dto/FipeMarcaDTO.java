package com.glc.smartcar.fipe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FipeMarcaDTO(@JsonProperty("name")String nome, @JsonProperty("code") String codigo) {
}
