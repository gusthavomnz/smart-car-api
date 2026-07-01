package com.glc.smartcar.integration.fipe.adapter;

import com.glc.smartcar.integration.fipe.Fipe;
import com.glc.smartcar.integration.fipe.FipeRepository;
import com.glc.smartcar.integration.fipe.dto.FipeNameAndCode;
import com.glc.smartcar.integration.fipe.dto.FipeVeiculoDTO;
import com.glc.smartcar.integration.fipe.port.FipePort;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class FipeAdapter implements FipePort {

    private static final DateTimeFormatter FIPE_MES_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt", "BR"));

    private final FipeClient fipeClient;
    private final FipeRepository fipeRepository;
    private final FipeMapper fipeMapper;

    public FipeAdapter(FipeClient fipeClient, FipeRepository fipeRepository, FipeMapper fipeMapper) {
        this.fipeClient = fipeClient;
        this.fipeRepository = fipeRepository;
        this.fipeMapper = fipeMapper;
    }

    @Override
    public List<FipeNameAndCode> buscarMarcas() {
        return fipeClient.buscarMarcas();
    }

    @Override
    public List<FipeNameAndCode> buscarModelos(String brandId) {
        return fipeClient.buscarModelos(brandId);
    }

    @Override
    public List<FipeNameAndCode> buscarAnos(String brandId, String modelId) {
        return fipeClient.buscarAnos(brandId, modelId);
    }

    @Override
    public FipeVeiculoDTO obterPreco(String brandId, String modelId, String yearId) {
        Fipe fipeCache = fipeRepository.findByCodigoMarcaAndCodigoModeloAndCodigoAno(brandId, modelId, yearId)
                .orElse(null);

        if (fipeCache != null && verificarDataCache(fipeCache)) {
            System.out.println("PEGUEI DO BANCO!");
            return fipeMapper.toDTO(fipeCache);
        }

        FipeVeiculoDTO dto = fipeClient.obterPreco(brandId, modelId, yearId);

        if (fipeCache == null || !dto.MesReferencia().equalsIgnoreCase(fipeCache.getReferenciaMes())) {

            // ADICIONE ESTA VERIFICAÇÃO:
            // Se não encontrou pela Marca/Modelo, busca usando a restrição da V9
            if (fipeCache == null) {
                fipeCache = fipeRepository.findByCodigoFipeAndCodigoAno(dto.CodigoFipe(), yearId).orElse(null);
            }

            Fipe fipeAtualizado = fipeMapper.toEntity(dto, brandId, modelId, yearId, fipeCache);
            fipeRepository.save(fipeAtualizado);
        }

        return dto;
    }

    public boolean verificarDataCache(Fipe fipeCache) {
        if (fipeCache.getReferenciaMes() == null) {
            return false;
        }
        try {
            YearMonth mesAnoCache = YearMonth.parse(fipeCache.getReferenciaMes().toLowerCase(), FIPE_MES_FORMATTER);
            return mesAnoCache.equals(YearMonth.now());
        } catch (Exception e) {
            return false;
        }
    }
}
