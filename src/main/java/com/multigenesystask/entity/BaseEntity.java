package com.multigenesystask.entity;

import java.time.LocalDateTime;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    private LocalDateTime createdAt;
}