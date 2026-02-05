package com.intelliquest.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDTO {
    private String username;
    private String email;
    private String currentPassword;
    private String newPassword;
    private MultipartFile profilePic;
}
