package com.book.flaschenbook.dto;

import lombok.Data;

@Data
public class LogoutRequestDTO {
    private int userId;
    private String sessionId;
}
