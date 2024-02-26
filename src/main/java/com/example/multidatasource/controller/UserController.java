package com.example.multidatasource.controller;

import com.example.multidatasource.model.User;
import com.example.multidatasource.service.UserService;
import com.example.multidatasource.sharding.ShardingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final ShardingService shardingService;

    //  curl -X POST -i -H "Content-Type:application/json" -d '{"name": "FrodoBaggins", "gender": true}' http://localhost:8080/api/user
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<User> newUser(@RequestBody User user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService
                        .saveUser(user)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "User can not be saved"
                        ))
                );
    }

    //  curl -X POST -i -H "Content-Type:application/json" -d '{"name": "FrodoBaggins", "gender": true}' http://localhost:8080/api/multiuser
    // sql test/test@(description=(address=(host=localhost)(protocol=tcp)(port=1521))(connect_data=(service_name=xepdb1)))
    @RequestMapping(value = "/multiuser", method = RequestMethod.POST)
    public ResponseEntity<User> multiNewUser(@RequestBody User user) throws Exception {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService
                        .multiSaveUser(user)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "User can not be saved"
                        ))
                );
    }

    //  curl -X GET -i -H "Content-Type:application/json" http://localhost:8080/api/user/1
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> findUserById(@PathVariable("id") Long id,
                                             @RequestHeader(value = "id", required = false) Long optionalHeader) throws Exception {
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

    //  curl -X GET -i -H "Content-Type:application/json" http://localhost:8080/api/usersbygender?gender=true
    @RequestMapping(value = "/usersbygender", method = RequestMethod.GET)
    public ResponseEntity<List<User>> findByGender(
            @RequestParam(defaultValue = "true") Boolean gender) throws Exception {
        return ResponseEntity.ok(userService.findByGender(gender));
    }
}
