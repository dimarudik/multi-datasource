package com.example.multidatasource.sharding;

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
                dataSourceContextHolder.setContext("datasource1");
                break;
            case 2:
                dataSourceContextHolder.setContext("datasource2");
                break;
            default:
                throw new RuntimeException("Unknown ShardId");
        }
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
