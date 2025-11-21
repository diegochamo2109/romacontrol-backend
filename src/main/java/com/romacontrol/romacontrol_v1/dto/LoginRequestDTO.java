package com.romacontrol.romacontrol_v1.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String dni;
    private String pin;
}
