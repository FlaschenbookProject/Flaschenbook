package com.book.flaschenbook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Data
@Embeddable
public class SessionDataId implements Serializable {

    private int userId;
    private String sessionId;

    public SessionDataId(int userId, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
    }

    public SessionDataId(){

    }

}