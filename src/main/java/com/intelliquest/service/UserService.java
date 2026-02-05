package com.intelliquest.service;

import com.intelliquest.dto.UserUpdateRequestDTO;
import com.intelliquest.model.User;
import com.intelliquest.model.UserProfilePicture;
import com.intelliquest.repository.UserProfilePictureRepository;
import com.intelliquest.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfilePictureRepository userProfilePictureRepository;


    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserProfilePictureRepository userProfilePictureRepository) {
this.userRepository = userRepository;
this.passwordEncoder = passwordEncoder;
this.userProfilePictureRepository = userProfilePictureRepository;
}


    public void registerUser(String name, String email, String password, User.UserRole role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        String hashedPassword = passwordEncoder.encode(password);
        
        User newUser = new User();
        newUser.setUsername(name);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);
        newUser.setRole(role);
        newUser.setEnabled(true);
        userRepository.save(newUser);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean resetPassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
    
    public boolean deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            return false;
        }

        try {
            userRepository.deleteById(userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public boolean updateUserProfile(String email, UserUpdateRequestDTO updateRequest) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();

        if (updateRequest.getUsername() != null) {
            user.setUsername(updateRequest.getUsername());
        }

        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }

        // Handle password change
        if (updateRequest.getCurrentPassword() != null && updateRequest.getNewPassword() != null) {
            if (!passwordEncoder.matches(updateRequest.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Incorrect current password.");
            }
            user.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
        }

        // Handle profile pic (save to local disk or cloud)
        if (updateRequest.getProfilePic() != null && !updateRequest.getProfilePic().isEmpty()) {
            try {
                String filename = "profile_" + user.getId() + "_" + System.currentTimeMillis() + ".jpg";
                Path uploadPath = Paths.get("uploads/profile-pictures/");
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(filename);
                updateRequest.getProfilePic().transferTo(filePath);

                // Save filename in UserProfilePicture table
                userProfilePictureRepository.findByUserId(user.getId())
                    .ifPresentOrElse(existing -> {
                        existing.setFileName(filename);
                        userProfilePictureRepository.save(existing);
                    }, () -> {
                        UserProfilePicture profilePic = new UserProfilePicture();
                        profilePic.setUserId(user.getId());
                        profilePic.setFileName(filename);
                        userProfilePictureRepository.save(profilePic);
                    });

            } catch (IOException e) {
                throw new RuntimeException("Failed to save profile picture", e);
            }
        }


        userRepository.save(user);
        return true;
    }
    
    public Optional<UserProfilePicture> getProfilePictureByUserId(Long userId) {
        return userProfilePictureRepository.findByUserId(userId);
    }



}
