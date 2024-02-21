package com.example.multidatasource.service;

import com.example.multidatasource.datasource.DataSourceContext;
import com.example.multidatasource.datasource.DataSourceContextHolder;
import com.example.multidatasource.model.User;
import com.example.multidatasource.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final DataSourceContextHolder dataSourceContextHolder;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> saveUser(User user) {
        return Optional.of(userRepository.save(user));
    }

    public Optional<User> multiSaveUser(User user) throws Exception {
        try (AutoCloseable a = dataSourceContextHolder.setContext(DataSourceContext.CLIENT_A)){
            userRepository.save(user);
        }
        try (AutoCloseable a = dataSourceContextHolder.setContext(DataSourceContext.CLIENT_B)){
            return Optional.of(userRepository.save(user));
        }
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Page<User> findAll(int pageNo, int pageSize, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return userRepository.findAll(pageable);
    }
}
