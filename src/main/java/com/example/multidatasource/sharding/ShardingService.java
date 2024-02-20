package com.example.multidatasource.sharding;

import com.example.multidatasource.datasource.DataSourceContext;
import com.example.multidatasource.datasource.DataSourceContextHolder;
import com.example.multidatasource.datasource.DataSourceMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ShardingService {
    private final DataSourceContextHolder dataSourceContextHolder;
    private final DataSourceMap dataSourceMap;

    public void setDataSourceContextByShardId(int shardId) {
        switch (shardId) {
            case 1:
                dataSourceContextHolder.setContext(DataSourceContext.CLIENT_A);
                break;
            case 2:
                dataSourceContextHolder.setContext(DataSourceContext.CLIENT_B);
                break;
            default:
                throw new RuntimeException("Unknown ShardId");
        }
        log.info("Current Context is: {}", dataSourceContextHolder.getContext());
    }

    public void setDataSourceContextByValue(int value) {
//        log.info("DataSourceMap size : {}", dataSourceMap.size());
        log.info("value {}", value);
        log.info("getShardId(dataSourceMap.size(), value) {}", getShardId(dataSourceMap.size(), value));
        switch (getShardId(dataSourceMap.size(), value)) {
            case 1:
                dataSourceContextHolder.setContext(DataSourceContext.CLIENT_A);
                break;
            case 2:
                dataSourceContextHolder.setContext(DataSourceContext.CLIENT_B);
                break;
            default:
                throw new RuntimeException("Unknown ShardId");
        }
        log.info("Current Context is: {}", dataSourceContextHolder.getContext());
    }

    public int getShardId(int shards, int value) {
        int v = Math.abs(value);
        int l = (int) Math.pow(2, logBase2(shards));
        if (v % l <= shards - l && v % l > 0) {
            return v % (l * 2);
        } if (v % l == 0) {
            return l;
        }
        return v % l;
    }

    private int logBase2(int n) {
        return (int) (Math.log(n) / Math.log(2));
    }
}
