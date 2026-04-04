package com.glc.smartcar.fipe;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Fipe {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "marca", nullable = false)
        private String marca;

        @Column(name = "modelo", nullable = false)
        private String modelo;

        @Column(name = "ano", nullable = false)
        private int ano;

        @Column(name = "codigo_fipe", unique = true)
        private String codigoFipe;

}

