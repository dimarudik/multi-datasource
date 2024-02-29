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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final OutboxRepository outboxRepository;
    private final DataSourceContextHolder dataSourceContextHolder;
    private final TransactionTemplate transactionTemplate;
    private final DataSourceMap dataSourceMap;
    private final HikariProperties hikariProperties;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> saveUser(User user) {
        return Optional.of(userRepository.save(user));
    }

    public Optional<User> multiSaveUser(User user) {
        Outbox outbox = new Outbox();

        Optional<User> optionalUser = transactionTemplate.execute(transactionStatus -> {
            Optional<User> o = Optional.of(userRepository.save(user));
            try {
                ObjectMapper objectMapper = JsonMapper.builder()
                        .findAndAddModules()
                        .build();
                outbox.setMessage(objectMapper.writeValueAsString(o.get()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            outboxRepository.save(outbox);
            return o;
        });

        if (dataSourceMap.hasDataSource(hikariProperties.getTarget())) {
            try (AutoCloseable a = dataSourceContextHolder.setContext(hikariProperties.getTarget())) {
                assert Objects.requireNonNull(optionalUser).isPresent();
                userRepository.save(optionalUser.get());
            } catch (Exception e) {
                log.error("", e);
                return optionalUser;
            }
            outboxRepository.delete(outbox);
        }

        return optionalUser;
    }

    public Optional<User> findUserById(Long id) throws Exception {
        try (AutoCloseable a = dataSourceContextHolder.setContext(hikariProperties.getSource())){
            return userRepository.findById(id);
        }
    }

    public Page<User> findAll(int pageNo, int pageSize, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return userRepository.findAll(pageable);
    }

    public List<User> findByGender(Boolean gender) throws Exception{
        return userRepository.findByGender(gender);
    }
}
