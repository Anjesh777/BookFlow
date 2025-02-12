package com.BookFlow.bookflow.dto;

import lombok.*;

import java.util.UUID;

@Data
@Getter
@Setter
public class UserDetailsResponse extends UserDetailsDTO{

    private String user_id;
    private String created_at;

}
