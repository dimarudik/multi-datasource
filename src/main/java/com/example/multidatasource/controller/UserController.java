package com.example.multidatasource.controller;

import com.example.multidatasource.datasource.DataSourceContextHolder;
import com.example.multidatasource.datasource.DataSourceMap;
import com.example.multidatasource.model.User;
import com.example.multidatasource.service.UserService;
import com.example.multidatasource.sharding.ShardingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final ShardingService shardingService;
    private final RestTemplate restTemplate;

    //  curl -X POST -i -H "Content-Type:application/json" -d '{"name": "FrodoBaggins"}' http://localhost:8080/api/user
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<User> newUser(@RequestBody User user) {
        shardingService.setDataSourceContextByValue(user.getName().hashCode());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService
                        .saveUser(user)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "User can not be saved"
                        ))
                );
    }

    //  curl -X POST -i -H "Content-Type:application/json" -d '{"name": "FrodoBaggins"}' http://localhost:8080/api/userext
    @RequestMapping(value = "/userext", method = RequestMethod.POST)
    public ResponseEntity<User> newUserExt(@RequestBody User user) {
        ResponseEntity<User> user_CLIENT_A = restTemplate.postForEntity("http://localhost:8080/api/v1/user?shardId=1", user, User.class);
        return restTemplate.postForEntity("http://localhost:8080/api/v1/user?shardId=2", user, User.class);
    }

    //  curl -X GET -i -H "Content-Type:application/json" http://localhost:8080/api/user/1
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> findUserById(@PathVariable("id") Long id,
                                             @RequestHeader(value = "id", required = false) Long optionalHeader) {
        if (optionalHeader != null) {
            System.out.println(optionalHeader);
        }
        return ResponseEntity.ok(
                userService
                        .findUserById(id)
                        .orElseThrow(EntityNotFoundException::new)
        );
    }

    //  curl -X GET -i -H "Content-Type:application/json" http://localhost:8080/api/user
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<List<User>> findAllUsers() {
        return ResponseEntity.ok(
                userService
                        .findAllUsers()
        );
    }

    //  curl -X GET -i -H "Content-Type:application/json" http://localhost:8080/api/users?shardId=1
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<User>> findAllByPageAndSort(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(defaultValue = "1") int shardId) {
        shardingService.setDataSourceContextByShardId(shardId);
        Page<User> result = userService.findAll(pageNo, pageSize, sortBy, sortDirection);
        return ResponseEntity.ok(result.getContent());
    }
}
