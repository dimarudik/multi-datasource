package com.example.multidatasource.datasource;

import com.example.multidatasource.config.HikariProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@Getter
public class DataSourceRouter extends AbstractRoutingDataSource {
    private final DataSourceContextHolder dataSourceContextHolder;
    private final HikariProperties hikariProperties;

    public DataSourceRouter(DataSourceContextHolder dataSourceContextHolder,
                            DataSourceMap dataSourceMap,
                            HikariProperties hikariProperties) {
        this.hikariProperties = hikariProperties;
        this.dataSourceContextHolder = dataSourceContextHolder;
        Map<Object, Object> map = dataSourceMap.initMap();
        this.setTargetDataSources(map);
        this.setDefaultTargetDataSource(map.get(this.hikariProperties.getSource()));
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return dataSourceContextHolder.getContext();
    }
}
