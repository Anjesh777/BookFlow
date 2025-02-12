package com.BookFlow.bookflow.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserDetailsDTO {

private String fullname;
private String email;
private String phone;
private String account;
private String role;
private boolean status;
private String createdby;
private boolean is_main_user;


}
