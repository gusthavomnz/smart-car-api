package com.glc.smartcar.integration.fipe.adapter;

import com.glc.smartcar.integration.fipe.Fipe;
import com.glc.smartcar.integration.fipe.dto.FipeVeiculoDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class FipeMapper {

    public FipeVeiculoDTO toDTO(Fipe fipe) {
        String valorFormatado = fipe.getPreco() != null
                ? "R$ " + String.format("%,.2f", fipe.getPreco()).replace('.', '#').replace(',', '.').replace('#', ',')
                : null;
        return new FipeVeiculoDTO(
                valorFormatado,
                fipe.getMarca(),
                fipe.getModelo(),
                fipe.getAno(),
                fipe.getCombustivel(),
                fipe.getCodigoFipe(),
                fipe.getReferenciaMes()
        );
    }

    public Fipe toEntity(FipeVeiculoDTO dto, String codigoMarca, String codigoModelo, String codigoAno, Fipe existente) {
        Fipe fipe = existente != null ? existente : new Fipe();
        fipe.setMarca(dto.Marca());
        fipe.setModelo(dto.Modelo());
        fipe.setAno(dto.AnoModelo());
        fipe.setCodigoFipe(dto.CodigoFipe());
        fipe.setCodigoMarca(codigoMarca);
        fipe.setCodigoModelo(codigoModelo);
        fipe.setCodigoAno(codigoAno);
        fipe.setCombustivel(dto.Combustivel());
        fipe.setReferenciaMes(dto.MesReferencia());
        fipe.setPreco(parsearPreco(dto.Valor()));
        fipe.setAtualizadoA(LocalDateTime.now());
        return fipe;
    }

    private BigDecimal parsearPreco(String valor) {
        if (valor == null) return null;
        String limpo = valor.replaceAll("[^\\d,]", "").replace(",", ".");
        try {
            return new BigDecimal(limpo);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
