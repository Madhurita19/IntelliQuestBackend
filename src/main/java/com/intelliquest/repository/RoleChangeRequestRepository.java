package com.intelliquest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.intelliquest.model.RoleChangeRequest;

public interface RoleChangeRequestRepository extends JpaRepository<RoleChangeRequest, Long> {
	    Optional<RoleChangeRequest> findByUserId(Long userId);
	}


