package com.romacontrol.romacontrol_v1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.romacontrol.romacontrol_v1.model.RegistroTerminal;

public interface RegistroTerminalRepository extends JpaRepository<RegistroTerminal, Long> {

    /**
     * 🔹 Busca la terminal abierta más reciente (si existe).
     * SELECT * FROM registro_terminal WHERE terminal_abierta = true ORDER BY id DESC LIMIT 1
     */
    Optional<RegistroTerminal> findFirstByTerminalAbiertaTrueOrderByIdDesc();
}