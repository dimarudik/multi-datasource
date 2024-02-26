package com.example.multidatasource.datasource;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class DataSourceContextHolder {
    private static ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public AutoCloseable setContext(String dataSourceContext) {
        Assert.notNull(dataSourceContext, "clientDatabase cannot be null");
        CONTEXT.set(dataSourceContext);
        return () -> CONTEXT.remove();
    }

    public String getContext() {
        return CONTEXT.get();
    }

    public void clear() {
        CONTEXT.remove();
    }
}
