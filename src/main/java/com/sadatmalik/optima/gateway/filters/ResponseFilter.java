package com.sadatmalik.optima.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

/**
 * Ensures that all egress responses have the correlation id inserted in the http headers.
 *
 * @author sadatmalik
 */
@Slf4j
@Configuration
public class ResponseFilter {

    @Autowired
    FilterUtils filterUtils;

    /**
     * Grabs the correlation ID that was passed in to the original HTTP request and injects it
     * into the response, logging the outgoing request URI so that you have “bookends” that show
     * the incoming and outgoing entry of the user’s request into the gateway.
     *
     * @return
     */
    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                String correlationId = filterUtils.getCorrelationId(requestHeaders);
                log.debug("Adding the correlation id to the outbound headers. {}", correlationId);
                exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, correlationId);
                log.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
            }));
        };
    }
}
