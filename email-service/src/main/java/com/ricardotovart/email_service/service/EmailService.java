package com.ricardotovart.email_service.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


//    private final JavaMailSender mailSender;
//    private final TemplateEngine templateEngine;
//
//    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
//        this.mailSender = mailSender;
//        this.templateEngine = templateEngine;
//    }

    @Value("${app.mail.from}")
    private String fromEmail;

    /**
     * Envía el correo de bienvenida usando una plantilla Thymeleaf HTML.
     *
     * @param toEmail   email del destinatario
     * @param fullName  nombre completo para personalizar el saludo
     */
    public void sendWelcomeEmail(String toEmail, String fullName) {

        System.out.println("[Email] Enviando correo de bienvenida a=" + toEmail + " | fullName=" + fullName);
//        try {
//            // 1. Construir el contexto con las variables de la plantilla
//            Context context = new Context();
//            context.setVariable("fullName", fullName);
//            context.setVariable("email", toEmail);
//
//            // 2. Renderizar la plantilla HTML (resources/templates/welcome-email.html)
//            String htmlContent = templateEngine.process("welcome-email", context);
//
//            // 3. Construir el mensaje MIME (soporte para HTML)
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setFrom(fromEmail);
//            helper.setTo(toEmail);
//            helper.setSubject("¡Bienvenido a nuestra plataforma!");
//            helper.setText(htmlContent, true); // true = es HTML
//
//            // 4. Enviar
//            mailSender.send(message);
//
//            log.info("[Email] Correo de bienvenida enviado a={}", toEmail);
//
//        } catch (MessagingException e) {
//            log.error("[Email] Error al enviar correo a={} | error={}", toEmail, e.getMessage());
//            // Re-lanzamos para que el consumer pueda manejarlo (DLT, reintento, etc.)
//            throw new RuntimeException("Error al enviar email de bienvenida", e);
//        }
    }
}
