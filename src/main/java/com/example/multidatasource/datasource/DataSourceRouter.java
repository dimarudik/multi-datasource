package com.example.multidatasource.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class DataSourceRouter extends AbstractRoutingDataSource {
    private final DataSourceContextHolder dataSourceContextHolder;

    public DataSourceRouter(DataSourceContextHolder dataSourceContextHolder,
                            DataSourceMap dataSourceMap) {
        this.dataSourceContextHolder = dataSourceContextHolder;
        Map<Object, Object> map = dataSourceMap.initMap();
        this.setTargetDataSources(map);
        this.setDefaultTargetDataSource(map.get("datasource1"));
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return dataSourceContextHolder.getContext();
    }
}
