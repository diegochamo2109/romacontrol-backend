// util/BcryptGen.java (puede estar en test o main, lo ejecutas desde el IDE)
package com.romacontrol.romacontrol_v1.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BcryptGen {
  public static void main(String[] args) {
    PasswordEncoder pe = new BCryptPasswordEncoder(12);
    System.out.println(pe.encode("admin123")); // cambia por el PIN que quieras
  }
}
