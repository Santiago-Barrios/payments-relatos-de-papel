package com.unir.payments.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    /**
     * RestClient.Builder sin LoadBalancer - usado por Eureka para su comunicación directa
     * con el servidor. Si no existe este bean, Eureka usa el @LoadBalanced y falla al
     * interpretar "localhost" como service-id.
     */
    @Bean
    @Primary
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * RestClient.Builder con LoadBalancer - para llamadas a otros microservicios
     * usando el nombre registrado en Eureka (ej: http://search/...)
     */
    @Bean("loadBalancedRestClientBuilder")
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }
}
