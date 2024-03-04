package com.example.multidatasource.model;

import lombok.Data;

import java.time.Instant;
@Data
public class UserDto {
    private long id;
    private String name;
    private Instant createAt;
    private Instant updateAt;
    private Boolean gender;
}
