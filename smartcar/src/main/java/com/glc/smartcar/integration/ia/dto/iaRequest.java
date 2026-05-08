package com.glc.smartcar.integration.ia.dto;


import java.util.List;


public record iaRequest(
     String model,
     List<Message> messages,
     Double temperature
){}
