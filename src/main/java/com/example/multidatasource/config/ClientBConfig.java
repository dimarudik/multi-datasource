package com.example.multidatasource.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="clientb.datasource")
@Data
public class ClientBConfig {
    private String url;
    private String password;
    private String username;
}
