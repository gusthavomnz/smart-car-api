package com.glc.smartcar.avaliacoes;

import com.glc.smartcar.avaliacoes.enums.HistoricoAtivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacoesRepository extends JpaRepository<Avaliacoes,Long> {

    List<Avaliacoes> findAllByUsuarioIdAndHistoricoAtivo(Long usuarioId, HistoricoAtivo historicoAtivo);
}
