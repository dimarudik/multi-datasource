package com.example.multidatasource.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataSourceRouter extends AbstractRoutingDataSource {
    private final DataSourceContextHolder dataSourceContextHolder;

    public DataSourceRouter(DataSourceContextHolder dataSourceContextHolder,
                            DataSourceMap dataSourceMap) {
        this.dataSourceContextHolder = dataSourceContextHolder;
        Map<Object, Object> map = dataSourceMap.initMap();
        this.setTargetDataSources(map);
        this.setDefaultTargetDataSource(map.get(DataSourceContext.CLIENT_A));
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return dataSourceContextHolder.getContext();
    }
}
