package com.glc.smartcar.avaliacoes;

import com.glc.smartcar.avaliacoes.enums.Conservacao;
import com.glc.smartcar.avaliacoes.enums.HistoricoAtivo;
import com.glc.smartcar.avaliacoes.enums.StatusResultado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "fipe_id", nullable = false)
    private String fipeId;

    @Column(name = "preco_desejado", nullable = false)
    private Double precoDesejado;

    @Column(name = "kms_rodados", nullable = false)
    private Double kmsRodados;

    @Column(name = "preco_fipe", nullable = false)
    private Double precoFipe;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_resultado", nullable = false)
    private StatusResultado statusResultado;

    @Column(name = "criado_a", updatable = false)
    private LocalDateTime criado_a;

    @Enumerated(EnumType.STRING)
    @Column(name = "conservacao", nullable = false)
    private Conservacao conservacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "historico_ativo", nullable = false)
    private HistoricoAtivo historicoAtivo;

    @Column(name = "notas_pessoais")
    private String notasPessoais;


}
