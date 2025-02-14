package com.BookFlow.bookflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterDTO extends CompanyFilterDTO{

    private UUID user_id;
    private String role;
    private String fullname;
    private String email;
    private String phone;
    private String account;
    private LocalDate created_at;
    private boolean main_user;


}
