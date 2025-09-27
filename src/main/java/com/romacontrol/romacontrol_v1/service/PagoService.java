package com.romacontrol.romacontrol_v1.service;

import com.romacontrol.romacontrol_v1.dto.PagoCreateRequest;
import com.romacontrol.romacontrol_v1.dto.PagoDetailResponse;

public interface PagoService {
  /** Registra un pago validando que la cuota asignada al usuario no esté ya PAGADA. */
  PagoDetailResponse registrarPago(PagoCreateRequest req, String usernameActual);
}
