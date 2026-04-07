package com.example.jwt.auth.kafka.producer;

import com.example.jwt.auth.kafka.event.UserRegisteredEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
@Component
public class UserEventProducer {


    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${app.kafka.topics.user-registered}")
    private String userRegisteredTopic;

    /**
     * Publica el evento de registro en Kafka de forma asíncrona.
     *
     * La KEY del mensaje es el email del usuario:
     * → Garantiza que todos los eventos del mismo usuario
     *   vayan siempre a la misma partición (orden garantizado por usuario).
     */
    public void publishUserRegistered(String email, String fullName) {
        UserRegisteredEvent event = UserRegisteredEvent.of(email, fullName);

        CompletableFuture<SendResult<String, UserRegisteredEvent>> future =
                kafkaTemplate.send(userRegisteredTopic, email, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                System.out.println(
                        "[Kafka] Error al publicar UserRegisteredEvent para email=" + email + " | error=" + ex.getMessage()
                );
            } else {
                System.out.println(
                        "[Kafka] UserRegisteredEvent publicado | email=" + email + " | topic=" + result.getRecordMetadata().topic() + " | partition=" + result.getRecordMetadata().partition() + " | offset=" + result.getRecordMetadata().offset()
                );
            }
        });
    }
}
