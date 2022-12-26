package com.itech.api.form.response.drive;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.api.services.drive.model.About;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DriveInfoResponse {

    public DriveInfoResponse(About about) {
        this.user = new User(about.getUser());
        this.storageQuota = new StorageQuota(about.getStorageQuota());
    }

    @JsonInclude(Include.NON_NULL)
    private User user;

    @JsonInclude(Include.NON_NULL)
    private StorageQuota storageQuota;
    
    @Data
    class User{
        public User(com.google.api.services.drive.model.User user) {
            this.displayName = user.getDisplayName();
            this.emailAddress = user.getEmailAddress();
            this.kind = user.getKind();
            this.me = user.getMe();
            this.permissionId = user.getPermissionId();
            this.photoLink = user.getPhotoLink();
        }

        @JsonInclude(Include.NON_NULL)
        private String kind;

        @JsonInclude(Include.NON_NULL)
        private String displayName;

        @JsonInclude(Include.NON_NULL)
        private String photoLink;

        @JsonInclude(Include.NON_NULL)
        private Boolean me;

        @JsonInclude(Include.NON_NULL)
        private String permissionId;

        @JsonInclude(Include.NON_NULL)
        private String emailAddress;
    }
    
    @Data
    class StorageQuota{
        public StorageQuota(com.google.api.services.drive.model.About.StorageQuota storageQuota) {
            this.limit = storageQuota.getLimit();
            this.usage = storageQuota.getUsage();
            this.usageInDrive = storageQuota.getUsageInDrive();
            this.usageInDriveTrash = storageQuota.getUsageInDriveTrash();
        }
        @JsonInclude(Include.NON_NULL)
        private Long limit;
        @JsonInclude(Include.NON_NULL)
        private Long usage;
        @JsonInclude(Include.NON_NULL)
        private Long usageInDrive;
        @JsonInclude(Include.NON_NULL)
        private Long usageInDriveTrash;
    }
}
