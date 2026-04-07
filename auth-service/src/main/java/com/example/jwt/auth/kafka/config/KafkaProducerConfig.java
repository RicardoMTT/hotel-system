package com.example.jwt.auth.kafka.config;

import com.example.jwt.auth.kafka.event.UserRegisteredEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Configuración del producer:
     * - Key:   String  (usamos el email como clave para garantizar orden por usuario)
     * - Value: JSON    (serialización automática del record UserRegisteredEvent)
     */
    @Bean
    public ProducerFactory<String, UserRegisteredEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Serialización
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // No incluir info de tipo Java en el header del mensaje
        // (el consumer no necesita conocer la clase exacta del producer)
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        // Garantiza que el mensaje fue recibido por todos los brokers disponibles
        // En local solo hay 1 broker, por lo que "all" = "1"
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        // Reintentos automáticos ante fallos transitorios de red
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        // Evita mensajes duplicados en caso de reintento
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * KafkaTemplate: la abstracción de Spring para enviar mensajes.
     * Se inyectará en el UserEventProducer.
     */
    @Bean
    public KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
