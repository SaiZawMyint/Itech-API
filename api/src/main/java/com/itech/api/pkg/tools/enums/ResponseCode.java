package com.itech.api.pkg.tools.enums;

import lombok.Getter;

@Getter
public enum ResponseCode {
    EMPTY("Empty response", 204), ERROR("Error response", 500), SUCCESS("Get data success", 200),
    UPDATE_SUCCESS("Update request success", 201), REQUIRED("Missing required field!", 450),
    DELETE("Delete data success", 202), EMPTY_CONTENT("No content response",204),
    UNAUTHORIZED("Unauthorized!",401),
    BAD_REQUEST("Bad credential request!",412),
    REGIST_REQUEST_ACCEPT("Registration request success!",201),
    CREATED("Created request success",201),
    SPREADSHEET_CREATED("Create new spreadsheet success.",201),
    SPREADSHEET_IMPORT("Import spreadsheet success.",201),
    SHEET_CREATED("Created new sheet success.",201),
    REQUIRED_AUTH("Please authorize your crediential first!",403),
    TOKEN_EXPIRED("Access token has been expired!",401);

    private final String message;
    private final Integer code;

    ResponseCode(final String string, final Integer code) {
        this.message = string;
        this.code = code;
    }

}
