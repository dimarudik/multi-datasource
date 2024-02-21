package com.example.multidatasource.datasource;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class DataSourceContextHolder {
    private static ThreadLocal<DataSourceContext> CONTEXT = new ThreadLocal<>();

    public AutoCloseable setContext(DataSourceContext dataSourceContext) {
        Assert.notNull(dataSourceContext, "clientDatabase cannot be null");
        CONTEXT.set(dataSourceContext);
        return () -> CONTEXT.remove();
    }

    public DataSourceContext getContext() {
        return CONTEXT.get();
    }

    public void clear() {
        CONTEXT.remove();
    }
}
