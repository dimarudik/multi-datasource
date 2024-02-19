package com.example.multidatasource.datasource;

import com.example.multidatasource.config.ClientAConfig;
import com.example.multidatasource.config.ClientBConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class DataSourceMap {
    @Getter
    private final Map<Object, Object> map = new HashMap<>();
    private final ClientAConfig clientAConfig;
    private final ClientBConfig clientBConfig;

    public DataSource dataSourceClientA() {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(clientAConfig.getUrl());
        hikariConfig.setUsername(clientAConfig.getUsername());
        hikariConfig.setPassword(clientAConfig.getPassword());
        return new HikariDataSource(hikariConfig);
    }

    public DataSource dataSourceClientB() {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(clientBConfig.getUrl());
        hikariConfig.setUsername(clientBConfig.getUsername());
        hikariConfig.setPassword(clientBConfig.getPassword());
        return new HikariDataSource(hikariConfig);
    }

    public Map<Object, Object> initMap() {
        if (map.isEmpty()) {
            map.put(DataSourceContext.CLIENT_A, dataSourceClientA());
            map.put(DataSourceContext.CLIENT_B, dataSourceClientB());
        }
        return map;
    }
}
