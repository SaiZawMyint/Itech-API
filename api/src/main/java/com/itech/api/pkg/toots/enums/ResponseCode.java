package com.itech.api.pkg.toots.enums;

import lombok.Getter;

@Getter
public enum ResponseCode {
    EMPTY("Empty response", 204), ERROR("Error response", 500), SUCCESS("Get data success", 200),
    UPDATE_SUCCESS("Update request success", 201), REQUIRED("Missing required field!", 450),
    DELETE("Delete data success", 202), EMPTY_CONTENT("No content response",204);

    private final String message;
    private final Integer code;

    ResponseCode(final String string, final Integer code) {
        this.message = string;
        this.code = code;
    }

}
