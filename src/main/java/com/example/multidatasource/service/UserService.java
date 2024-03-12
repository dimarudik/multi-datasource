package com.example.multidatasource.service;

import com.example.multidatasource.config.HikariProperties;
import com.example.multidatasource.datasource.DataSourceContextHolder;
import com.example.multidatasource.datasource.DataSourceMap;
import com.example.multidatasource.model.Outbox;
import com.example.multidatasource.model.User;
import com.example.multidatasource.model.UserDto;
import com.example.multidatasource.model.mapper.UserMapper;
import com.example.multidatasource.repository.OutboxRepository;
import com.example.multidatasource.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMethod;

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
    private final DataSourceMap dataSourceMap;
    private final HikariProperties hikariProperties;
    private final UserMapper userMapper;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> saveUser(User user) {
        return Optional.of(userRepository.save(user));
    }

    public Optional<User> updateUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow();
        userMapper.updateUserFromDto(userDto, user);
        return Optional.of(userRepository.save(user));
    }

    public Optional<User> multiSaveUser(User user, RequestMethod method) {
        Outbox outbox = new Outbox();
        outbox.setMethod(method);

        Optional<User> optionalUser = transactionTemplate.execute(transactionStatus -> {
            Optional<User> o = saveUser(user);
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
                saveUser(user);
            } catch (Exception e) {
                log.error("", e);
                return optionalUser;
            }
            outboxRepository.delete(outbox);
        }

        return optionalUser;
    }

    public Optional<User> multiUpdateUser(UserDto userDto, RequestMethod method) {
        Outbox outbox = new Outbox();
        outbox.setMethod(method);

        Optional<User> optionalUser = transactionTemplate.execute(transactionStatus -> {
            Optional<User> o = updateUser(userDto);
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
                updateUser(userDto);
            } catch (Exception e) {
                log.error("", e);
                return optionalUser;
            }
            outboxRepository.delete(outbox);
        }

        return optionalUser;
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Page<User> findAll(int pageNo, int pageSize, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return userRepository.findAll(pageable);
    }

    public List<User> findByGender(Boolean gender) {
        return userRepository.findByGender(gender);
    }

    public Optional<User> findCustomUserById(Long id) throws Exception {
        return userRepository.findCustomUserById(id);
    }
}
