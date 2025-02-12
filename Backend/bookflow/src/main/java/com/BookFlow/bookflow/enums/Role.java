package com.BookFlow.bookflow.enums;

public enum Role {

    COMPANY_ADMIN,
    COMPANY_SUPERADMIN,
    COMPANY_USER;

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

}
