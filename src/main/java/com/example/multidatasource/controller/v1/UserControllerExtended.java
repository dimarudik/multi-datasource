package com.example.multidatasource.controller.v1;

import com.example.multidatasource.model.User;
import com.example.multidatasource.service.UserService;
import com.example.multidatasource.sharding.ShardingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@AllArgsConstructor
public class UserControllerExtended {
    private final UserService userService;
    private final ShardingService shardingService;

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<User> newUser(@RequestBody User user, @RequestParam(defaultValue = "1") int shardId) {
        shardingService.setDataSourceContextByShardId(shardId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService
                        .saveUser(user)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "User can not be saved"
                        ))
                );
    }
}
