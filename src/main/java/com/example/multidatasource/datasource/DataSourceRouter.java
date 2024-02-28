package com.example.multidatasource.datasource;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@Getter
public class DataSourceRouter extends AbstractRoutingDataSource {
    private final String sourceValue;
    private final String targetValue;
    private final DataSourceContextHolder dataSourceContextHolder;

    public DataSourceRouter(DataSourceContextHolder dataSourceContextHolder,
                            DataSourceMap dataSourceMap,
                            @Value("${source.value}") String sourceValue,
                            @Value("${target.value}") String targetValue) {
        this.dataSourceContextHolder = dataSourceContextHolder;
        this.sourceValue = sourceValue;
        this.targetValue = targetValue;
        Map<Object, Object> map = dataSourceMap.initMap();
        this.setTargetDataSources(map);
        this.setDefaultTargetDataSource(map.get(this.sourceValue));
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return dataSourceContextHolder.getContext();
    }
}
