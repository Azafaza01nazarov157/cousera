package org.example.cursera.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.example.cursera.config.props.CorsConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.example.cursera.config.props.CorsProp;

/**
 * Configuration for web-related settings and filters.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer {
    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;
    private final CorsProp corsProp;
    private final CorsConfigurationProperties corsProperties;

    /**
     * Constructor for WebConfigurer.
     *
     * @param env      The Spring environment.
     * @param corsProp The CORS properties.
     */
    public WebConfigurer(Environment env, CorsProp corsProp, CorsConfigurationProperties corsPropConf) {
        this.env = env;
        this.corsProp = corsProp;
        this.corsProperties = corsPropConf;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Initializes the servlet context, logging active profiles and configuration status.
     *
     * @param servletContext The servlet context.
     * @throws ServletException If servlet context initialization fails.
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (env.getActiveProfiles().length != 0) {
            log.info("Web application configuration, using profiles: {}",
                    (Object[]) env.getActiveProfiles());
        }
        log.info("Web application fully configured");
    }

    /**
     * Configure CORS (Cross-Origin Resource Sharing) filter.
     *
     * @return The configured CORS filter.
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOrigin(corsProperties.getAllowedOriginsBase());
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/management/**", config);
        source.registerCorsConfiguration("/v3/api-docs", config);

        log.debug("Registering CORS filter with allowed origins: {}", corsProperties.getAllowedOriginsBase());

        return new CorsFilter(source);
    }
}
