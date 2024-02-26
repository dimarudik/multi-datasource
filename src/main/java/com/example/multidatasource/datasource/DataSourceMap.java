package com.example.multidatasource.datasource;

import com.example.multidatasource.config.HikariProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class DataSourceMap {
    @Getter
    private final Map<Object, Object> map = new HashMap<>();
    private final HikariProperties hikariProperties;

    public Map<Object, Object> initMap() {
        hikariProperties.getConfig().forEach((k, v) -> map.put(k, new HikariDataSource(v)));
        return map;
    }

    public int size() {
        return map.size();
    }
}
