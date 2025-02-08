package com.BookFlow.bookflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {


//    @NotBlank(message = "Title is required")
//    @Size(min = 3, message = "Title must be at least 3 characters long")
    private String title;

//    @NotBlank(message = "Message is required")
//    @Size(min = 10, message = "Message must be at least 10 characters long")
    private String message;

//    @NotBlank(message = "Target audience is required")
    private String targetAudience;

//    @NotBlank(message = "Notification type is required")
    private String notificationType;



}
