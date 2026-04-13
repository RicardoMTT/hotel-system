# Spring boot application with Authentication jwt and docker

## Descripción
Este proyecto implementa un servicio de autenticación (auth-service) usando Spring Boot 3, Spring Security y JWT.
La autenticación es stateless y no utiliza OAuth2 ni Keycloak.

## Tipo de autenticación usada
JWT personalizado (custom JWT authentication)

Este proyecto:

❌ No usa OAuth2

❌ No usa Keycloak

✅ Usa JWT generado y validado manualmente con Spring Security

## Endpoints

  - Login

  - Register

### Dockerizing application

  - Dockerfile
    
    Stage n°1, generara un jar que contendrá toda la aplicación spring boot.
    
    Stage n°2, Copia el JAR generado en la primera etapa, lo renombra con el nombre app.jar y luego ejecuta ese jar que iniciara la aplicación Spring Boot.
    
  - docker-compose.yml

    Levanta 2 servicios, uno que creara un contenedor de una imagen de Mysql , y otra que levantara un contenedor de la imagen que se creo en el Dockerfile



### Kafka
Una vez levantado el contenedor de kafka, tendremos que crear los topicos de kafka

Entramos al contenedor:
docker exec -it kafka /bin/bash

Creamos los topicos:
Topico de pagos

/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic payment.confirmed --partitions 3 --replication-factor 1



Topico de usuarios
/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic user.registered --partitions 3 --replication-factor 1

Listar topicos
/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list