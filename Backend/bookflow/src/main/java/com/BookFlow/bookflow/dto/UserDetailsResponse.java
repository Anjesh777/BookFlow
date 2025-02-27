package com.BookFlow.bookflow.dto;

import lombok.*;

import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse extends UserDetailsDTO{

    private String user_id;
    private String created_at;

    public UserDetailsResponse(UUID userId, String fullname, String email, String phone, String role,String account,boolean status) {
        this.user_id = userId.toString();
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.account = account;
        this.status = status;
    }


}
