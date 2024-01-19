package com.api.farmingsoon.domain.member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {

    ADMIN("ROLE_ADMIN"),
    MANAGER("ROLE_MANAGER");

    private final String value;

    public static MemberRole checkMemberRole(String role) {
        return switch (role) {
            case "ADMIN" -> ADMIN;
            case "MANAGER" -> MANAGER;
            default -> null;
        };
    }
}