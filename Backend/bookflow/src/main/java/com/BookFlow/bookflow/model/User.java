package com.BookFlow.bookflow.model;

import com.BookFlow.bookflow.enums.Role;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID user_id;
    @ManyToOne
    @JoinColumn(name = "company_id",nullable = false)
    private Company company_id;

    @Column(unique = false)
    @Nullable
    private String fullname;
    @Nullable
    private String phone;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @CreationTimestamp
    private LocalDate date;
    @Column(name = "is_enable")
    private boolean is_enabled = true;
    @Column(name = "update_at")
    @Nullable
    private LocalDate update_at;
    @Column(name = "is_main_user")
    private Boolean mainuser=false;



    public User(UUID user_id, @Nullable String fullname, String email, @Nullable String phone, Role role, LocalDate date,boolean mainuser) {
        this.user_id = user_id;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.date = date;
        this.mainuser=mainuser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
