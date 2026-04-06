package com.ricardotovart.email_service.kafka.consumer;


import com.ricardotovart.email_service.kafka.event.UserRegisteredEvent;
import com.ricardotovart.email_service.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;


@Component
public class UserRegisteredConsumer {


    private EmailService emailService;

    public UserRegisteredConsumer(EmailService emailService) {
        this.emailService = emailService;
    }
    /**
     * @RetryableTopic: reintenta automáticamente ante fallos
     * - attempts=3: máximo 3 intentos (1 original + 2 reintentos)
     * - backoff: espera 3s, luego 6s entre reintentos (exponencial)
     * - Si los 3 fallan → el mensaje va al Dead Letter Topic (user.registered.DLT)
     *
     * @KafkaListener: escucha el topic user.registered
     * - groupId: identifica este consumer group
     * - containerFactory: usa la config manual ack que definimos
     */
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 3000, multiplier = 2),
            dltTopicSuffix = ".DLT"
    )
    @KafkaListener(
            topics = "${app.kafka.topics.user-registered}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onUserRegistered(
            @Payload UserRegisteredEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {

        System.out.println("[Kafka] Evento recibido | email=" + event.email() + " | partition=" + partition + " | offset=" + offset);

        try {
            emailService.sendWelcomeEmail(event.email(), event.fullName());

            // Confirmar procesamiento exitoso — Kafka no re-entregará este mensaje
            acknowledgment.acknowledge();
            System.out.println("[Kafka] Evento procesado correctamente | email=" + event.email());

        } catch (Exception e) {

            System.out.println("[Kafka] Fallo al procesar evento | email=" + event.email() + " | error=" + e.getMessage());
            // No hacemos acknowledge → @RetryableTopic reintentará
            throw e;
        }
    }

    /**
     * DLT Handler: se ejecuta cuando el mensaje agotó todos los reintentos.
     * Aquí puedes: guardar en BD, enviar alerta, notificar al equipo, etc.
     */
    @DltHandler
    public void onDeadLetter(
            @Payload UserRegisteredEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
    ) {

        System.out.println("[Kafka] Mensaje enviado al Dead Letter Topic | topic=" + topic + " | email=" + event.email());
        // TODO: guardar en BD de fallos, enviar alerta al equipo, etc.
    }
}
