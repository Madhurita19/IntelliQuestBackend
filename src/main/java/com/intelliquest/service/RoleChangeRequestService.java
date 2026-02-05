package com.intelliquest.service;

import com.intelliquest.model.RoleChangeRequest;
import com.intelliquest.model.User;
import com.intelliquest.repository.RoleChangeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleChangeRequestService {

    private final RoleChangeRequestRepository requestRepository;

    public boolean requestRoleChange(Long userId, User.UserRole requestedRole) {
        Optional<RoleChangeRequest> existingRequest = requestRepository.findByUserId(userId);
        if (existingRequest.isPresent()) {
            throw new IllegalStateException("Role change request already submitted.");
        }

        RoleChangeRequest request = new RoleChangeRequest();
        request.setUserId(userId);
        request.setRequestedRole(requestedRole);
        request.setReviewed(false);
        requestRepository.save(request);
        return true;
    }
}
