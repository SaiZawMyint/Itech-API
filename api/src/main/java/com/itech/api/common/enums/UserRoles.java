package com.itech.api.common.enums;

import lombok.Getter;

@Getter
public enum UserRoles {
    ADMIN(1, "Admin"),
    USER(2, "User");

    private final int id;
    private final String desc;
    UserRoles(int id, String description) {
        this.id = id;
        this.desc = description;
    }
}
