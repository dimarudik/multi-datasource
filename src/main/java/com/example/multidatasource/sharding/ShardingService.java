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

    public void setDataSourceContextByShardId(int value) {
        switch (value) {
            case 1:
                dataSourceContextHolder.setContext(DataSourceContext.CLIENT_A);
                log.info("CLIENT_A");
                break;
            case 2:
                dataSourceContextHolder.setContext(DataSourceContext.CLIENT_B);
                log.info("CLIENT_B");
                break;
            default:
                throw new RuntimeException("Unknown ShardId");
        }
    }

    public void setDataSourceContextByValue(int value) {
        switch (getShardId(dataSourceMap.getMap().size(), value)) {
            case 1:
                dataSourceContextHolder.setContext(DataSourceContext.CLIENT_A);
                break;
            case 2:
                dataSourceContextHolder.setContext(DataSourceContext.CLIENT_B);
                break;
            default:
                throw new RuntimeException("Unknown ShardId");
        }
    }

    public int getShardId(int shards, int value) {
        int l = (int) Math.pow(2, logBase2(shards));
        if (value % l <= shards - l && value % l > 0) {
            return value % (l * 2);
        } if (value % l == 0) {
            return l;
        }
        return value % l;
    }

    private int logBase2(int n) {
        return (int) (Math.log(n) / Math.log(2));
    }
}
