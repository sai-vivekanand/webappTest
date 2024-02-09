package com.neu.cloud.cloudApp.repository;

import java.util.Optional;

import com.neu.cloud.cloudApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	User findByUsername(String username);

	Optional<User> findByUsernameAndPassword(String string, String string2);

}
