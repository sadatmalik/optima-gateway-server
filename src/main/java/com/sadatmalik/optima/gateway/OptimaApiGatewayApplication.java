package com.sadatmalik.optima.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * At its heart, the Spring Cloud Gateway is a reverse proxy. A reverse proxy is an
 * intermediate server that sits between the client trying to reach a resource and the
 * resource itself. The client has no idea it’s even communicating with a server. The
 * reverse proxy takes care of capturing the client’s request and then calls the remote
 * resource on the client’s behalf.
 *
 * To communicate with the upstream services, the gateway has to know how to map the
 * incoming call to the upstream route. The Spring Cloud Gateway has several mechanisms
 * to do this, including
 *
 * - Automated mapping of routes using service discovery
 * - Manual mapping of routes using service discovery
 *
 * For instance, if we want to call our organization service and use automated routing
 * via the Spring Cloud Gateway, we would have our client call the Gateway service
 * instance using the following URL as the endpoint:
 *
 * http://localhost:8072/organization-service/v1/organization/
 *
 * The beauty of using Spring Cloud Gateway with Eureka is that not only do we now have
 * a single endpoint through which we can make calls, but we can also add and remove
 * instances of a service without ever having to modify the gateway.
 *
 * If we want to see the routes managed by the Gateway server, we can list the routes
 * via the actuator/gateway/routes endpoint on the Gateway server.
 *
 * Spring Actuator exposes a POST-based endpoint route, actuator/gateway/refresh,
 * that will cause it to reload its route configuration.
 *
 * Predicate and Filter Factories
 * The Spring Cloud Gateway Predicate and Filter Factories can be used similarly to
 * Spring aspect classes. These can match or intercept a wide body of behaviors and
 * decorate or change the behavior of the call without the original coder being aware
 * of the change.
 *
 * While a servlet filter or Spring aspect is localized to a specific service, using
 * the Gateway and its Predicate and Filter Factories allows us to implement cross-
 * cutting concerns across all the services being routed through the gateway.
 * Predicates allow us to check if the requests fulfill a set of conditions before
 * processing the request.
 *
 * @author sadatmalik
 */
@SpringBootApplication
@EnableEurekaClient
public class OptimaApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(OptimaApiGatewayApplication.class, args);
	}

}
