package com.glc.smartcar.integration.fipe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FipeNameAndCode(@JsonProperty("name")String nome, @JsonProperty("code") String codigo) {
}
