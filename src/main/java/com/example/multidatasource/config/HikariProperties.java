package com.example.multidatasource.config;

import com.zaxxer.hikari.HikariConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "hikari")
@Data
public class HikariProperties {
    private String source;
    private String target;
    private Map<String, HikariConfig> config;
}
