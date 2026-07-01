package com.glc.smartcar.integration.fipe;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fipe", uniqueConstraints = {
    @UniqueConstraint(name = "uq_codigo_fipe_ano", columnNames = {"codigo_fipe", "codigo_ano"})
})
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

        @Column(name = "codigo_fipe")
        private String codigoFipe;

        @Column(name = "codigo_marca", nullable = false)
        private String codigoMarca;

        @Column(name = "codigo_modelo", nullable = false)
        private String codigoModelo;

        @Column(name = "codigo_ano", nullable = false)
        private String codigoAno;

        @Column(name = "preco", precision = 10, scale = 2)
        private java.math.BigDecimal preco;

        @Column(name = "combustivel", length = 30)
        private String combustivel;

        @Column(name = "referencia_mes", length = 30)
        private String referenciaMes;

        @Column(name = "atualizado_a")
        private java.time.LocalDateTime atualizadoA;

}

