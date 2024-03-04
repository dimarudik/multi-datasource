package com.example.multidatasource.model.generator;

import com.example.multidatasource.config.HikariProperties;
import com.example.multidatasource.datasource.DataSourceContextHolder;
import com.example.multidatasource.model.User;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.springframework.context.annotation.Bean;

public class UserIdGenerator extends SequenceStyleGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        if (dataSourceContextHolder().getContext() == null
                || dataSourceContextHolder().getContext().equals(hikariProperties().getSource())) {
            return super.generate(sharedSessionContractImplementor, o);
        }
        return ((User) o).getId();
    }

    @Bean
    private HikariProperties hikariProperties(){
        return new HikariProperties();
    }

    @Bean
    private DataSourceContextHolder dataSourceContextHolder() {
        return new DataSourceContextHolder();
    }
}
