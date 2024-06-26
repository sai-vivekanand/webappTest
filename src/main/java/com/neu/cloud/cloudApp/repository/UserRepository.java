package com.neu.cloud.cloudApp.repository;

import java.util.Optional;
import java.util.UUID;
import com.neu.cloud.cloudApp.model.User;
import com.neu.cloud.cloudApp.model.VerificationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> { // Change from Integer to UUID

	Optional<User> findByUsername(String username);
	Optional<User> findByUuid(UUID uuid);// Change the return type to Optional

	Optional<User> findByUsernameAndPassword(String username, String password);
}
