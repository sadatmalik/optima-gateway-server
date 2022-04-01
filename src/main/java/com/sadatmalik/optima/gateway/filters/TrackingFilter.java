package com.sadatmalik.optima.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filters implement the GlobalFilter interface and must override the filter() method.
 *
 * @author sadatmalik
 */
@Slf4j
@Order(1)
@Component
public class TrackingFilter implements GlobalFilter {

    @Autowired
    FilterUtils filterUtils;

    /**
     * Code that executes every time a request passes through the filter.
     *
     * Extracts the HTTP header from the request using the ServerWebExchange object passed
     * by parameters to the filter() method. This method contains the business logic that the
     * filter implements.
     *
     * The HTTP headers are obtained from the ServerWebExchange object.
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();

        if (isCorrelationIdPresent(requestHeaders)) {
            log.debug("tmx-correlation-id found in tracking filter: {}. ",
                    filterUtils.getCorrelationId(requestHeaders));
        } else {
            String correlationID = generateCorrelationId();
            exchange = filterUtils.setCorrelationId(exchange, correlationID);
            log.debug("tmx-correlation-id generated in tracking filter: {}. ",
                    correlationID);
        }

        return chain.filter(exchange);
    }


    /**
     * A helper method that checks if there’s a correlation ID in the request header.
     *
     * @param requestHeaders
     * @return
     */
    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        if (filterUtils.getCorrelationId(requestHeaders) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * A helper method that generates a correlation ID UUID value.
     * @return
     */
    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }

}