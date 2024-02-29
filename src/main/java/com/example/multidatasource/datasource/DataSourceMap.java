package com.example.multidatasource.datasource;

import com.example.multidatasource.config.HikariProperties;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class DataSourceMap {
    @Getter
    private final Map<Object, Object> map = new HashMap<>();
    private final HikariProperties hikariProperties;

    public Map<Object, Object> initMap() {
        hikariProperties.getConfig().forEach((k, v) -> {
            try {
                HikariDataSource hikariDataSource = new HikariDataSource(v);
                map.put(k, hikariDataSource);
            } catch (HikariPool.PoolInitializationException e) {
                if (k.equals(hikariProperties.getSource())) {
                    throw new RuntimeException();
                }
                log.error("Pool {} cannot be initialized", k);
            }
        });
        return map;
    }

    public int size() {
        return map.size();
    }

    public Boolean hasDataSource(String key) {
        return map.containsKey(key);
    }
}
