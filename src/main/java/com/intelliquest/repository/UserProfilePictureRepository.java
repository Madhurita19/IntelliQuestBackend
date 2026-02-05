package com.intelliquest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.intelliquest.model.UserProfilePicture;

public interface UserProfilePictureRepository extends JpaRepository<UserProfilePicture, Long> {
    Optional<UserProfilePicture> findByUserId(Long userId);
}
