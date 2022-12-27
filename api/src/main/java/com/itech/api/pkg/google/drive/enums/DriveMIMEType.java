package com.itech.api.pkg.google.drive.enums;

import lombok.Getter;

@Getter
public enum DriveMIMEType {
    AUDIO("application/vnd.google-apps.audio"),
    GOOGLE_DOC("application/vnd.google-apps.document"),
    DRIVE_SDK("application/vnd.google-apps.drive-sdk"),
    DRAWING("application/vnd.google-apps.drawing"),
    FILE("application/vnd.google-apps.file"),
    FOLDER("application/vnd.google-apps.folder"),
    FORM("application/vnd.google-apps.form"),
    FUSIONTABLE("application/vnd.google-apps.fusiontable"),
    JAM("application/vnd.google-apps.jam"),
    MAP("application/vnd.google-apps.map"),
    PHOTO("application/vnd.google-apps.photo"),
    PRESENTATION("application/vnd.google-apps.presentation"),
    SCRIPT("application/vnd.google-apps.script"),
    SHORTCUT("application/vnd.google-apps.shortcut"),
    SITE("application/vnd.google-apps.site"),
    SPREADSHEET("application/vnd.google-apps.spreadsheet"),
    UNKNOW("application/vnd.google-apps.unknown"),
    VIDEO("application/vnd.google-apps.video")
    ;

    private String type;
    
    DriveMIMEType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return this.type;
    }
}
