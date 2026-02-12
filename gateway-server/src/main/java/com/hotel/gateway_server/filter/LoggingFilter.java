package com.hotel.gateway_server.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.filter.GlobalFilter;

@Configuration
public class LoggingFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);


    @Bean
    public GlobalFilter logFilter() {
        return (exchange, chain) -> {

            long startTime = System.currentTimeMillis();

            String method = exchange.getRequest().getMethod().name();
            String path = exchange.getRequest().getURI().getPath();

            // log.info("➡️ Request IN  | method={} path={}", method, path);

            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        long duration = System.currentTimeMillis() - startTime;
                        int statusCode = exchange.getResponse().getStatusCode() != null
                                ? exchange.getResponse().getStatusCode().value()
                                : 0;

                        log.info("⬅️ Response OUT | method={} path={} status={} time={}ms",
                                method, path, statusCode, duration);
                    }));
        };
    }
}
