package com.glc.smartcar.core.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome",nullable = false)
    private String nome;

    @Column(name = "email",nullable = false, unique = true)
    private String email;

    @Column(name = "senha",nullable = false)
    private String senha;

    @Column(name = "criado_a")
    private LocalDateTime criado_a;
}
