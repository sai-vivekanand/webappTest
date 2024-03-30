package com.neu.cloud.cloudApp.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neu.cloud.cloudApp.model.VerificationInfo;

@Repository
public interface VerificationInfoRepository extends JpaRepository<VerificationInfo, UUID> {

    Optional<VerificationInfo> findByUserUuid(UUID userUuid);
}
