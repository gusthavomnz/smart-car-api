package com.glc.smartcar.core.avaliacoes;

import com.glc.smartcar.core.avaliacoes.enums.HistoricoAtivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacoesRepository extends JpaRepository<Avaliacoes,Long> {

    List<Avaliacoes> findAllByUsuarioIdAndHistoricoAtivo(Long usuarioId, HistoricoAtivo historicoAtivo);

    void deleteAllByUsuarioId(Long usuarioId);
}
