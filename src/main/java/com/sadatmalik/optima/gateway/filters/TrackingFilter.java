package com.sadatmalik.optima.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
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

        log.info("The authentication name from the token is : " +
                getUsername(requestHeaders));

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

    /**
     * Parses the token from the authorization HTTP header and pulls the preferred_username
     * from the JWT.
     *
     * NB: the AUTH_TOKEN variable in FilterUtils is set to Authorization.
     *
     * @param requestHeaders
     * @return
     */
    private String getUsername(HttpHeaders requestHeaders){
        String username = "";
        if (filterUtils.getAuthToken(requestHeaders)!=null){
            String authToken = filterUtils.getAuthToken(requestHeaders)
                    .replace("Bearer ","");
            JSONObject jsonObj = decodeJWT(authToken);
            try {
                username = jsonObj.getString("preferred_username");
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }
        return username;
    }

    /**
     * Uses Base64 encoding to parse the token, passing in the key that signs the token,
     * and parses the JWT body into a JSON object to retrieve the preferred_username.
     *
     * @param JWTToken
     * @return
     */
    private JSONObject decodeJWT(String JWTToken) {
        String[] split_string = JWTToken.split("\\.");
        String base64EncodedBody = split_string[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));
        return new JSONObject(body);
    }
}