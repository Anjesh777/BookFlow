package com.BookFlow.bookflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class NotificationDTO {
    private long id;
    private String title;
    private String message;
    private String targetAudience;
    private String notificationType;
    private LocalDateTime createdAt; }
