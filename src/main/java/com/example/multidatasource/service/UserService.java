package com.example.multidatasource.service;

import com.example.multidatasource.datasource.DataSourceContextHolder;
import com.example.multidatasource.datasource.DataSourceMap;
import com.example.multidatasource.datasource.DataSourceRouter;
import com.example.multidatasource.model.Outbox;
import com.example.multidatasource.model.User;
import com.example.multidatasource.repository.OutboxRepository;
import com.example.multidatasource.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final OutboxRepository outboxRepository;
    private final DataSourceContextHolder dataSourceContextHolder;
    private final TransactionTemplate transactionTemplate;
    private final DataSourceRouter dataSourceRouter;
    private final DataSourceMap dataSourceMap;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> saveUser(User user) {
        return Optional.of(userRepository.save(user));
    }

    public Optional<User> multiSaveUser(User user) {
        Optional<User> optionalUser = Optional.empty();
        Outbox outbox = new Outbox();

        if (dataSourceMap.getMap().containsKey(dataSourceRouter.getSourceValue())) {
            try (AutoCloseable a = dataSourceContextHolder.setContext(dataSourceRouter.getSourceValue())) {
                optionalUser = transactionTemplate.execute(transactionStatus -> {
                    Optional<User> o = Optional.of(userRepository.save(user));
                    outbox.setMessage(user.toString());
                    outboxRepository.save(outbox);
                    return o;
                });
            } catch (Exception e) {
                log.error("", e);
                throw new RuntimeException(e);
            }
        }

        if (dataSourceMap.getMap().containsKey(dataSourceRouter.getTargetValue())) {
            try (AutoCloseable a = dataSourceContextHolder.setContext(dataSourceRouter.getTargetValue())) {
                userRepository.save(user);
            } catch (Exception e) {
                log.error("", e);
                return optionalUser;
            }
        } else {
            return optionalUser;
        }

        if (dataSourceMap.getMap().containsKey(dataSourceRouter.getSourceValue())) {
            try (AutoCloseable a = dataSourceContextHolder.setContext(dataSourceRouter.getSourceValue())) {
                outboxRepository.delete(outbox);
            } catch (Exception e) {
                log.error("", e);
                throw new RuntimeException(e);
            }
        }
        return optionalUser;
    }

    public Optional<User> findUserById(Long id) throws Exception {
        try (AutoCloseable a = dataSourceContextHolder.setContext(dataSourceRouter.getSourceValue())){
            return userRepository.findById(id);
        }
    }

    public Page<User> findAll(int pageNo, int pageSize, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return userRepository.findAll(pageable);
    }

    public List<User> findByGender(Boolean gender) throws Exception{
        List<User> users;
        try (AutoCloseable a = dataSourceContextHolder.setContext(dataSourceRouter.getTargetValue())) {
            users = userRepository.findByGender(gender);
        }
        try (AutoCloseable a = dataSourceContextHolder.setContext(dataSourceRouter.getSourceValue())) {
            users = userRepository.findByGender(gender);
        }
        return users;
    }
}
