package com.example.multidatasource.model;

import com.example.multidatasource.model.generator.UserIdGenerator;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "users_tab")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_tab_seq")
    @GenericGenerator(name = "users_tab_seq", type = UserIdGenerator.class)
    private long id;
    private String name;
    @CreationTimestamp
    private Instant createAt;
    @UpdateTimestamp
    private Instant updateAt;
    private Boolean gender;
}
