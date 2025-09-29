package com.romacontrol.romacontrol_v1.service;

import com.romacontrol.romacontrol_v1.dto.PagoResponse;
import com.romacontrol.romacontrol_v1.dto.RegistroPagoSolicitud;

public interface PagoService {
    PagoResponse registrarPago(RegistroPagoSolicitud solicitud, String usuarioLogueado);
}
