package com.BookFlow.bookflow.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class UserDetailsDTO {

public String fullname;
public String email;
public String phone;
public String account;
public String role;
public boolean status;
public String createdby;
public boolean is_main_user;


}
