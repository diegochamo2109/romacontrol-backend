package com.romacontrol.romacontrol_v1.service.impl.cierre;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.cierre.CierreCompletoHistorialResponse;
import com.romacontrol.romacontrol_v1.model.cierre.CierreCompleto;
import com.romacontrol.romacontrol_v1.repository.cierre.CierreCompletoRepository;
import com.romacontrol.romacontrol_v1.service.cierre.CierreCompletoHistorialService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CierreCompletoHistorialServiceImpl implements CierreCompletoHistorialService {

    private final CierreCompletoRepository cierreCompletoRepository;

    @Override
    public List<CierreCompletoHistorialResponse> historial(LocalDate desde, LocalDate hasta) {

        // Convertimos a OffsetDateTime
        var inicio = desde.atStartOfDay().atOffset(ZoneOffset.UTC);
        var fin = hasta.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        // Obtener lista desde BD
        List<CierreCompleto> resultados = 
                cierreCompletoRepository.findByFechaHoraCierreBetween(inicio, fin);

        // Mapear a DTO correcto
        return resultados.stream()
                .map(cc -> CierreCompletoHistorialResponse.builder()
                        .id(cc.getId())
                        .fechaHoraCierre(cc.getFechaHoraCierre()
                                .toLocalDateTime()
                                .format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm")))
                        .totalGeneral(cc.getTotalGeneral())
                        .cerradoPor(
                                cc.getCerradoPor().getPersona().getApellido() + ", " +
                                cc.getCerradoPor().getPersona().getNombre()
                        )
                        .build()
                )
                .toList();
    }
}
