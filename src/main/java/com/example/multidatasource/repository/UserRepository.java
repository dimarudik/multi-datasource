package com.example.multidatasource.repository;

import com.example.multidatasource.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByGender(Boolean gender);

    @Query("select user from User user where id = ?1")
    Optional<User> findCustomUserById(Long id);
}
