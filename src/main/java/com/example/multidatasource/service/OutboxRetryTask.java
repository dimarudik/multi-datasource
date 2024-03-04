package com.example.multidatasource.service;

import com.example.multidatasource.config.HikariProperties;
import com.example.multidatasource.datasource.DataSourceContextHolder;
import com.example.multidatasource.datasource.DataSourceMap;
import com.example.multidatasource.model.Outbox;
import com.example.multidatasource.model.User;
import com.example.multidatasource.repository.OutboxRepository;
import com.example.multidatasource.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
public record OutboxRetryTask(OutboxRepository outboxRepository,
                              UserRepository userRepository,
                              EntityManager entityManager,
                              TransactionTemplate transactionTemplate,
                              DataSourceContextHolder dataSourceContextHolder,
                              DataSourceMap dataSourceMap,
                              HikariProperties hikariProperties) {
//    @Scheduled(fixedDelayString = "5000")
    public void retry() throws JsonProcessingException {
        if (dataSourceMap.hasDataSource(hikariProperties.getTarget())) {
            try {

                Outbox outbox = outboxRepository.findFirstByOrderByCreateAt();

                if (outbox != null) {
                    ObjectMapper objectMapper = JsonMapper.builder()
                            .findAndAddModules()
                            .build();
                    User user = objectMapper.readValue(outbox.getMessage(), User.class);

                    saveUserToTarget(user);

                    transactionTemplate.executeWithoutResult(transactionStatus -> {
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        outboxRepository.delete(outbox);
                    });

                }
            } catch (ObjectOptimisticLockingFailureException e) {
                log.error("Message has already removed.");
            }
        }
    }

    private void saveUserToTarget(User user) {
        try (AutoCloseable a = dataSourceContextHolder.setContext(hikariProperties.getTarget())) {
            userRepository.save(user);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
