package com.romacontrol.romacontrol_v1.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String rawPin = "1234";
        String hash = encoder.encode(rawPin);
        System.out.println("Nuevo hash para PIN 1234:");
        System.out.println(hash);
    }
}
