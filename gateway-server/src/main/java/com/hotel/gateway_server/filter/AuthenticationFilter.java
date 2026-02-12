package com.hotel.gateway_server.filter;

import com.hotel.gateway_server.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final RouteValidator routeValidator;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    public AuthenticationFilter(JwtUtil jwtUtil, RouteValidator routeValidator) {
        this.jwtUtil = jwtUtil;
        this.routeValidator = routeValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Verificar si la ruta requiere autenticación
        if (routeValidator.isSecured.test(request)) {
            log.info("requiere token de acceso " + request.getURI().getPath());

            // Verificar que el header Authorization exista
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Falta el header de autorización", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    // Validar el token
                    if (!jwtUtil.validateToken(token)) {
                        return onError(exchange, "Token inválido o expirado", HttpStatus.UNAUTHORIZED);
                    }

                    // Extraer información del usuario y agregarla al header
                    String username = jwtUtil.extractUsername(token);

                    // Opcional: agregar información del usuario a los headers para los microservicios
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Email", username)
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());

                } catch (Exception e) {
                    return onError(exchange, "Token inválido: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
                }
            } else {
                return onError(exchange, "Formato de token inválido", HttpStatus.UNAUTHORIZED);
            }
        }

        log.info("No requiere token de acceso " + request.getURI().getPath());

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorBody = String.format("{\"error\": \"%s\", \"status\": %d}", message, status.value());
        DataBuffer buffer = response.bufferFactory().wrap(errorBody.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1; // Prioridad alta para ejecutarse primero
    }
}
