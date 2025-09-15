package com.romacontrol.romacontrol_v1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  // Usamos el mismo remitente que el usuario SMTP configurado
  @Value("${spring.mail.username}")
  private String mailFrom;

  @Async
  public void enviarBienvenida(String destinatario, String dni, String pinPlano) {
    if (destinatario == null || destinatario.isBlank()) return; // no rompe el alta
    try {
      SimpleMailMessage msg = new SimpleMailMessage();
      msg.setTo(destinatario);
      msg.setFrom("Gym Roma <" + mailFrom + ">");
      msg.setSubject("Nuevo Usuario Gym Roma");
      msg.setText(
          "Gracias por registrarte.\n\n" +
          "Tu usuario es: " + dni + "\n" +
          "Tu contraseña (PIN): " + pinPlano + "\n\n" +
          "Por seguridad, te recomendamos cambiarla en tu próximo ingreso."
      );
      mailSender.send(msg);
    } catch (Exception ex) {
      // Evitamos que una excepción asíncrona burbujee y ensucie logs
      System.err.println("Error enviando email a " + destinatario + ": " + ex.getMessage());
    }
  }
}
