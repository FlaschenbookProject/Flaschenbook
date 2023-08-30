package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "SessionData")
public class SessionDataEntity {

    @EmbeddedId
    private SessionDataId sessionDataId;
    private LocalDateTime sessionStart;
    private LocalDateTime sessionEnd;

}

