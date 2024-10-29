package org.example.cursera.config.props;

public interface EndpointsProp {
    String ALL = "/**";
    String AUTH = "/api/v1/auth";
    String AUTH_ALL = "/api/v1/auth";
    String BEARER = "Bearer ";
    String AUTH_ROLE = "/api/v1/**";
    String[] SWAGGER = {"/v3/api-docs", "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui/**", "/swagger-ui.html"};
}
