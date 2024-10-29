package org.example.cursera.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application.cors")
public class CorsConfigurationProperties {
    private String allowedOriginsBase;
}
