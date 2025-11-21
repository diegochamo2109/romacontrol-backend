package com.romacontrol.romacontrol_v1.dto.respuesta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDiaResponse {
    private Integer dia;
    private Long cantidadPagos;
}
