package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.UserDetailsDTO;
import com.BookFlow.bookflow.enums.Role;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.repository.company.CompanyRepo;
import com.BookFlow.bookflow.utils.customException.DuplicateFieldException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private CompanyRepo companyRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailVerificactionService emailVerificactionService;


    @Transactional
    public void registerUser(UserDetailsDTO userDetailsDTO){

        Optional<User> CompanyDetails =userRepo.findByUsername(userDetailsDTO.getCreatedby());
        String defaultPassword = generateRandomPassword(8);

        try {
            User user = new User();
            user.setCompany_id(CompanyDetails.get().getCompany_id());
            user.setEmail(userDetailsDTO.getEmail());
            user.setUsername(userDetailsDTO.getAccount());
            user.setFullname(userDetailsDTO.getFullname());
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setRole(Role.valueOf(userDetailsDTO.getRole()));
            user.setDate(LocalDateTime.now());
            user.setPhone(userDetailsDTO.getPhone());
            user.setMainuser(false);

            userRepo.save(user);
            emailVerificactionService.createUserCredentialsAndVerification(userDetailsDTO, defaultPassword);


        }catch (DuplicateFieldException e) {
            throw new DuplicateFieldException("Error Duplicate user");
        }
        catch (Exception e) {
            throw new RuntimeException("Error creating user", e);
        }

    }

    @Transactional
    public List<User> getAllCmpUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> currentUser = userRepo.findByUsername(currentUsername);

        if(currentUser.isEmpty()) {
            throw new RuntimeException("Current user not found");
        }

        // Get the company ID from the user's company object
        Company userCompany = currentUser.get().getCompany_id();
        UUID companyId = userCompany.getCompany_id();


        return userRepo.findByCompanyId(companyId);
    }

    public void updateUserDetails(UUID userId, String username, String email, String phone, String fullname, Role role, boolean isEnable, LocalDateTime updateDate ){
        userRepo.updateUser(userId,username,email,phone,fullname,role,isEnable,updateDate);
    }





    private String generateRandomPassword(int length) {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*()_-+=<>?";

        String allChars = upperCase + lowerCase + numbers + specialChars;
        Random random = new Random();

        StringBuilder password = new StringBuilder();

        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        for (int i = password.length(); i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }






}
