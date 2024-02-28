package com.example.multidatasource.service;

import com.example.multidatasource.model.Outbox;
import com.example.multidatasource.repository.OutboxRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
public record OutboxRetryTask(OutboxRepository outboxRepository,
                              UserService userService,
                              EntityManager entityManager,
                              TransactionTemplate transactionTemplate) {
//    @Scheduled(fixedDelayString = "1000")
    public void retry() {
        try {
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                Outbox outbox = outboxRepository.findFirstByOrderByCreateAt();
                if (outbox != null) {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    outboxRepository.delete(outbox);
                }
            });
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Message has already removed.");
        }
    }
}
