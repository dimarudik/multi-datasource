package com.example.multidatasource.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_tab")
@Data
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private RequestMethod method;
    private String message;
    @CreationTimestamp
    private Instant createAt;
    @Version
    private Long version;
}
