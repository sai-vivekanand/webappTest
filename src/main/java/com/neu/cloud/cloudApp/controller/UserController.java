package com.neu.cloud.cloudApp.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.neu.cloud.cloudApp.Utils.AuthHandler;
import com.neu.cloud.cloudApp.model.VerificationInfo;
import com.neu.cloud.cloudApp.repository.UserRepository;
import com.neu.cloud.cloudApp.repository.VerificationInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.neu.cloud.cloudApp.service.UserService;
import com.neu.cloud.cloudApp.model.User;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private AuthHandler authHandler;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private VerificationInfoRepository verificationInfoRepository;

	@PostMapping("/v1/user")
	public ResponseEntity<Map<String, Object>> createUser(@RequestBody(required = false) Map<String, String> requMap) {
		try {
			logger.info("This is a Post v1/user/self structured log message to check INFO");
			logger.error("This is a Post v1/user/self structured log message to check ERROR");
			logger.debug("This is a Post v1/user/self structured log message to check DEBUG");
			return userService.save(requMap);
		} catch (Exception e) {
			Map<String, Object> resMap = new HashMap<>();
			resMap.put("msg", "Please enter valid input");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400));
		}
	}

	@GetMapping("/v1/user/self")
	public ResponseEntity<Map<String, Object>> getAuthenticatedUser(HttpServletRequest httpServletRequest) {
		try {
			// The AuthHandler is assumed to extract the authenticated user's details
			User authUser = authHandler.getUser(httpServletRequest);
			if (authUser == null) {
				Map<String, Object> resMap = new HashMap<>();
				resMap.put("msg", "Unauthorized access. Please provide valid credentials.");
				logger.info("This is a Get v1/user/self structured log message to check INFO");
				logger.error("This is a Gut v1/user/self structured log message to check ERROR");
				logger.debug("This is a Gut v1/user/self structured log message to check DEBUG");
				return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(401));
			}

			// Use the authenticated user's ID to fetch the user details
			return userService.fetchById(String.valueOf(authUser.getId()), httpServletRequest);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Map<String, Object> resMap = new HashMap<>();
			resMap.put("msg", "An error occurred while fetching user data.");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400));
		}
	}


	@PutMapping("/v1/user/self")
	public ResponseEntity<Map<String, Object>> updateUser(@RequestBody Map<String, String> requMap, HttpServletRequest httpServletRequest) {
		try {
			User authUser = authHandler.getUser(httpServletRequest);
			if (authUser == null) {
				Map<String, Object> resMap = new HashMap<>();
				resMap.put("msg", "Unauthorized access. Please provide valid credentials.");
				logger.info("This is a Put v1/user/self structured log message to check INFO");
				logger.error("This is a Put v1/user/self structured log message to check ERROR");
				logger.debug("This is a Put v1/user/self structured log message to check DEBUG");
				return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(401));
			}
			return userService.updateUserById(String.valueOf(authUser.getId()), requMap, httpServletRequest);
		} catch (Exception e) {
			Map<String, Object> resMap = new HashMap<>();
			resMap.put("msg", "Please enter valid data");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400));
		}
	}

	@GetMapping("/v1/user/self/verify/")
	public ResponseEntity<Map<String, Object>> verifyUser(HttpServletRequest httpServletRequest) {
		try {
			// The AuthHandler is assumed to extract the authenticated user's details
			User authUser = authHandler.getUser(httpServletRequest);
			if (authUser == null) {
				Map<String, Object> resMap = new HashMap<>();
				resMap.put("msg", "Unauthorized access. Please provide valid credentials.");
				logger.info("This is a Get v1/user/self structured log message to check INFO");
				logger.error("This is a Gut v1/user/self structured log message to check ERROR");
				logger.debug("This is a Gut v1/user/self structured log message to check DEBUG");
				return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(401));
			}

			// Use the authenticated user's ID to fetch the user details
			return userService.fetchById(String.valueOf(authUser.getId()), httpServletRequest);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Map<String, Object> resMap = new HashMap<>();
			resMap.put("msg", "An error occurred while fetching user data.");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400));
		}
	}

	/*@GetMapping("/v1/user/verify_email/{uuid}")
	public ResponseEntity<Map<String, Object>> verifyUserEmail(@PathVariable String Uuid) {
		UUID uuid;
		uuid = UUID.fromString(Uuid.trim());
		try {
			Optional<VerificationInfo> verificationInfo = verificationInfoRepository.findById(uuid);

			if (!verificationInfo.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Verification token not found."));
			}

			VerificationInfo info = verificationInfo.get();
			long diffInMinutes = Duration.between(info.getEmailExpTimeTime().toInstant(), Instant.now()).toMinutes();

			if (diffInMinutes > 2) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Verification link expired."));
			}

			//String username = verificationInfo.
			User user = userRepository.findById(uuid);
			if (user == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found."));
			}

			user.setVerified(true);
			userRepository.save(user);

			return ResponseEntity.ok(Map.of("message", "User verified successfully."));
		} catch (Exception e) {
			logger.error("Error verifying user: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error during verification."));
		}
	}*/

	/*@GetMapping("/v1/user/verify_email/{uuid}")
	public ResponseEntity<Map<String, Object>> verifyUserEmail(@PathVariable String uuidString) {
		try {
			// Convert String to UUID
			UUID uuid = UUID.fromString(uuidString);

			// Use the UUID to find the verification info
			Optional<VerificationInfo> verificationInfo = verificationInfoRepository.findById(uuid);

			if (!verificationInfo.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Verification token not found."));
			}

			VerificationInfo info = verificationInfo.get();
			long diffInMinutes = Duration.between(info.getEmailExpTimeTime().toInstant(), Instant.now()).toMinutes();

			if (diffInMinutes > 2) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Verification link expired."));
			}

			// Find the user using UUID (You should implement this method in your repository)
			Optional<User> userOptional = userRepository.findByUuid(uuid);
			if (!userOptional.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found."));
			}

			User user = userOptional.get();
			user.setVerified(true);
			userRepository.save(user);

			return ResponseEntity.ok(Map.of("message", "User verified successfully."));
		} catch (IllegalArgumentException iae) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid UUID format."));
		} catch (Exception e) {
			logger.error("Error verifying user: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error during verification."));
		}
	}*/

	@GetMapping("/v1/user/verify_email")
	public ResponseEntity<Map<String, Object>> verifyUserEmail(@RequestParam("token") String uuidString) {
		System.out.println("uuid" + uuidString);
		try {
			// Convert String to UUID
			UUID uuid = UUID.fromString(uuidString);
			Optional<VerificationInfo> verificationInfoOpt = verificationInfoRepository.findById(uuid);

			if (!verificationInfoOpt.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Verification token not found."));
			}
			VerificationInfo verificationInfo = verificationInfoOpt.get();
			long diffInMinutes = Duration.between(verificationInfo.getEmailExpTimeTime().toInstant(), Instant.now()).toMinutes();

			if (diffInMinutes > 2) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Verification link expired."));
			}
			// Find the user by username obtained from verification info
			Optional<User> userOptional = userRepository.findByUsername(verificationInfo.getUsername());
			if (!userOptional.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found."));
			}

			User user = userOptional.get();
			user.setVerified(true);
			userRepository.save(user);

			return ResponseEntity.ok(Map.of("message", "User verified successfully."));
		} catch (IllegalArgumentException iae) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid UUID format."));
		} catch (Exception e) {
			logger.error("Error verifying user: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error during verification."));
		}
	}



}
