package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.CompanyDashbooksummaryDto;
import com.BookFlow.bookflow.dto.UserDetailsDTO;
import com.BookFlow.bookflow.dto.UserFilterDTO;
import com.BookFlow.bookflow.enums.Role;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.CashBookRepo;
import com.BookFlow.bookflow.repository.LedgerRepository;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.repository.UserRepoFilter;
import com.BookFlow.bookflow.repository.company.CompanyRepo;
import com.BookFlow.bookflow.repository.company.VerificationTokenRepo;
import com.BookFlow.bookflow.utils.customException.DuplicateFieldException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private CompanyRepo companyRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CashBookRepo cashBookRepo;
    @Autowired
    private LedgerRepository ledgerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailVerificactionService emailVerificactionService;
//    @Autowired
//    private TokenService tokenService;
    @Autowired
    private VerificationTokenRepo verificationTokenRepo;
    @Autowired
    private UserRepoFilter userRepoFilter;



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
            user.setDate(LocalDate.now());
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

    public void updateUserDetails(UUID userId, String username, String email, String phone, String fullname, Role role, boolean isEnable, LocalDate updateDate ){
        userRepo.updateUser(userId,username,email,phone,fullname,role,isEnable,updateDate);
    }


    @Transactional
    public void deleteUser(UUID uuid){
        verificationTokenRepo.deleteByUserId(uuid);
        userRepo.deleteByUserId(uuid);
    }

    public List<UserFilterDTO> findFilterUser(UserFilterDTO filter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Company userCompany = userRepo.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"))
                .getCompany_id();
        UUID companyId = userCompany.getCompany_id();

        System.out.println(filter);

        // Return all users if no filter
        if (filter == null) {
            return convertToDTO(userRepoFilter.findAll(companyId));
        }

        // Process date range
        // Process date range
        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (filter.getDateRange() != null &&
                filter.getDateRange().getFromDate() != null &&
                filter.getDateRange().getToDate() != null) {
            // Convert java.util.Date to LocalDate
            fromDate = filter.getDateRange().getFromDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            toDate = filter.getDateRange().getToDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        // Process search term
        String search = (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) ?
                filter.getSearch().trim() : null;

        // Process role
        Role role = null;
        if (filter.getRole() != null && !filter.getRole().trim().isEmpty()) {
            try {
                role = Role.valueOf(filter.getRole().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.error("Invalid role value: {}", filter.getRole());
                return Collections.emptyList();
            }
        }

        // Process status
        Boolean status = filter.getStatus();

        // Determine which filters are active
        boolean hasSearch = (search != null);
        boolean hasDateRange = (fromDate != null && toDate != null);
        boolean hasRole = (role != null);
        boolean hasStatus = (status != null);

        // Get and convert results based on filter combination
        List<User> users = switch (getFilterCombination(hasSearch, hasDateRange, hasRole, hasStatus)) {
            case "1111" -> userRepoFilter.findByAllFilters(companyId, search, role, status, fromDate, toDate);
            case "1000" -> userRepoFilter.findBySearch(companyId, search);
            case "0100" -> userRepoFilter.findByDateRange(companyId, fromDate, toDate);
            case "0010" -> userRepoFilter.findByRole(companyId, role);
            case "0001" -> userRepoFilter.findByStatus(companyId, status);
            case "1010" -> userRepoFilter.findBySearchAndRole(companyId, search, role);
            case "1001" -> userRepoFilter.findBySearchAndStatus(companyId, search, status);
            default -> userRepoFilter.findAll(companyId);
        };

        return convertToDTO(users);
    }

    private List<UserFilterDTO> convertToDTO(List<User> users) {
        return users.stream()
                .map(user -> {
                    UserFilterDTO dto = new UserFilterDTO();
                    dto.setUser_id(user.getUser_id());
                    dto.setFullname(user.getFullname());
                    dto.setEmail(user.getEmail());
                    dto.setPhone(user.getPhone());
                    dto.setRole(user.getRole().name());
                    dto.setStatus(user.is_enabled());
                    dto.setAccount(user.getUsername());
                    dto.setCreated_at(user.getDate());
                    dto.setMain_user(user.getMainuser());
                    // Set any other needed fields
                    return dto;
                })
                .collect(Collectors.toList());
    }


    private String getFilterCombination(boolean hasSearch, boolean hasDateRange,
                                        boolean hasRole, boolean hasStatus) {
        return String.format("%d%d%d%d",
                hasSearch ? 1 : 0,
                hasDateRange ? 1 : 0,
                hasRole ? 1 : 0,
                hasStatus ? 1 : 0
        );
    }



    public String generateRandomPassword(int length) {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*()_-+=<>?";

        String allChars = upperCase + lowerCase + numbers + specialChars;
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // Fill the rest of the password length with random characters
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


    public CompanyDashbooksummaryDto getSummary() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Company userCompany = userRepo.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"))
                .getCompany_id();
        UUID companyId = userCompany.getCompany_id();

        int numberOfUsersInCmp = userRepo.countUsersByCompanyId(companyId);
        int numberofCashbook = cashBookRepo.countUsersByCashbook(companyId);
        int numberofLedger = ledgerRepository.countUsersByLedger(companyId);
        int numberofServicebooked = 0; // You might want to implement this counter

        CompanyDashbooksummaryDto summaryDto = new CompanyDashbooksummaryDto();
        summaryDto.setTotalUsers(new BigDecimal(numberOfUsersInCmp));
        summaryDto.setTotalCashbook(new BigDecimal(numberofCashbook));
        summaryDto.setTotalLedger(new BigDecimal(numberofLedger));
        summaryDto.setServiceBooked(new BigDecimal(numberofServicebooked));

        return summaryDto;
    }
}
