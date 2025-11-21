package com.romacontrol.romacontrol_v1.service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.romacontrol.romacontrol_v1.model.Pago;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailFrom;

    // ================================
    // ✉️ EMAIL DE BIENVENIDA
    // ================================
    @Async
    public void enviarBienvenida(String destinatario, String dni, String pinPlano) {

        if (destinatario == null || destinatario.isBlank()) return;

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setFrom("Gym Roma <" + mailFrom + ">");
            helper.setSubject("Bienvenido/a a Gym Roma");

            // Logo desde /static/img
            ClassPathResource logoResource = new ClassPathResource("static/img/logologin2.png");
            String logoCid = "logoRoma";

            String html = """
                <div style="font-family:Arial; padding:20px; color:#333;">
                    <div style="text-align:center;">
                        <img src='cid:%s' style="width:150px; margin-bottom:15px;">
                        <h2 style="color:#ff7a00; margin:10px 0;">¡Bienvenido/a a Gym Roma!</h2>
                    </div>

                    <p>Gracias por registrarte en nuestro sistema.</p>

                    <p style="margin:10px 0; font-size:15px;">
                        <strong>DNI:</strong> %s <br>
                        <strong>PIN:</strong> %s
                    </p>

                    <p>Por seguridad, te recomendamos cambiar tu PIN en el primer inicio de sesión.</p>

                    <br><br>
                    <p style="text-align:center; font-size:12px; color:#777;">
                        RomaControl © 2025 – Sistema Integral de Gestión
                    </p>
                </div>
            """.formatted(logoCid, dni, pinPlano);

            helper.setText(html, true);
            helper.addInline(logoCid, logoResource);

            mailSender.send(mensaje);

        } catch (Exception ex) {
            System.err.println("Error enviando email bienvenida: " + ex.getMessage());
        }
    }

    // ================================
    // 📄 ENVIAR COMPROBANTE PDF A6
    // ================================
    @Async
    public void enviarComprobantePagoConPdf(Pago pago) {

        try {
            var usuario = pago.getUsuario();
            var persona = usuario.getPersona();

            if (persona.getEmail() == null || persona.getEmail().isBlank()) return;

            String archivo = "comprobante_pago_" + pago.getId() + ".pdf";
            File pdfFile = new File(System.getProperty("java.io.tmpdir"), archivo);

            generarPdfTicketA6(pago, pdfFile.getAbsolutePath());

            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(persona.getEmail());
            helper.setFrom("Gym Roma <" + mailFrom + ">");
            helper.setSubject("Comprobante de Pago - " + pago.getCuotaMensual().getDescripcion());

            String cuerpo = """
                Hola %s,

                Adjuntamos tu comprobante correspondiente al pago de la cuota '%s'.

                Importe: $%.2f
                Fecha: %s
                Hora: %s HS
                Método: %s
                Registrado por: %s

                RomaControl © 2025 – Sistema Integral de Gestión
            """.formatted(
                persona.getNombre(),
                pago.getCuotaMensual().getDescripcion(),
                pago.getMonto(),
                pago.getFechaPago().toLocalDate(),
                pago.getFechaPago().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                pago.getMetodoPago().getNombre(),
                pago.getCobradoPor().getPersona().getNombre() + " " + pago.getCobradoPor().getPersona().getApellido()
            );

            helper.setText(cuerpo, false);
            helper.addAttachment("Comprobante_" + persona.getApellido() + ".pdf", pdfFile);

            mailSender.send(mensaje);
            pdfFile.delete();

        } catch (Exception ex) {
            System.err.println("Error enviando comprobante PDF: " + ex.getMessage());
        }
    }

    // ================================
    // 🎟️ PDF A6 FORMATO TICKET PROFESIONAL
    // ================================
    private void generarPdfTicketA6(Pago pago, String ruta) throws Exception {

        Document doc = new Document(PageSize.A6);
        PdfWriter.getInstance(doc, new FileOutputStream(ruta));
        doc.open();

        // Paleta y estilos
        Font titulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font label = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);
        Font valor = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        BaseColor grisSuave = new BaseColor(220, 220, 220);

        // Logo Comprobante
        ClassPathResource logoResource = new ClassPathResource("static/img/logologin3.png");
        Image logo = Image.getInstance(logoResource.getInputStream().readAllBytes());
        logo.scaleToFit(120, 120);
        logo.setAlignment(Element.ALIGN_CENTER);
        doc.add(logo);

        doc.add(new Paragraph("\nCOMPROBANTE DE PAGO\n\n", titulo));

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{35, 65});

        añadirFila(tabla, "SOCIO:", pago.getUsuario().getPersona().getNombre() + " " + pago.getUsuario().getPersona().getApellido(), label, valor);
        añadirFila(tabla, "DNI:", pago.getUsuario().getDni(), label, valor);
        añadirFila(tabla, "CUOTA:", pago.getCuotaMensual().getDescripcion(), label, valor);
        añadirFila(tabla, "IMPORTE:", "$ " + pago.getMonto(), label, valor);
        añadirFila(tabla, "FECHA:", pago.getFechaPago().toLocalDate(), label, valor);
        añadirFila(tabla, "HORA:", pago.getFechaPago().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " HS", label, valor);
        añadirFila(tabla, "MÉTODO:", pago.getMetodoPago().getNombre(), label, valor);
        añadirFila(tabla, "COBRADO POR:", pago.getCobradoPor().getPersona().getNombre() + " " + pago.getCobradoPor().getPersona().getApellido(), label, valor);

        PdfPCell separador = new PdfPCell(new Phrase(""));
        separador.setColspan(2);
        separador.setBorder(Rectangle.BOTTOM);
        separador.setBorderWidth(1f);
        separador.setBorderColor(grisSuave);
        separador.setPaddingTop(8f);
        tabla.addCell(separador);

        añadirFila(tabla, "TOTAL:", "$ " + pago.getMonto(), label, valor);

        doc.add(tabla);
        doc.add(new Paragraph("\nRomaControl © 2025 – Sistema Integral de Gestión", valor));

        doc.close();
    }

    private void añadirFila(PdfPTable tabla, String nombre, Object valorObj, Font fLabel, Font fValor) {
        PdfPCell c1 = new PdfPCell(new Phrase(nombre, fLabel));
        PdfPCell c2 = new PdfPCell(new Phrase(String.valueOf(valorObj), fValor));

        c1.setBorder(Rectangle.NO_BORDER);
        c2.setBorder(Rectangle.NO_BORDER);

        c1.setPadding(4);
        c2.setPadding(4);

        tabla.addCell(c1);
        tabla.addCell(c2);
    }
}
