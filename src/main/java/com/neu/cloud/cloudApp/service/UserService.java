package com.neu.cloud.cloudApp.service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.neu.cloud.cloudApp.Utils.AuthHandler;
import com.neu.cloud.cloudApp.model.VerificationInfo;
import com.neu.cloud.cloudApp.repository.UserRepository;
import com.neu.cloud.cloudApp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.neu.cloud.cloudApp.Utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthHandler authHandler;

	@Autowired
	private Publisher publisher;

	@Autowired
	private ObjectMapper objectMapper;

	public ResponseEntity<Map<String, Object>> save(Map<String, String> requMap) {

		Map<String, Object> resMap = new HashMap<>();

		if (requMap.containsKey("first_name") == false || requMap.containsKey("last_name") == false
				|| requMap.containsKey("password") == false || requMap.containsKey("username") == false) {
			resMap.put("msg", "Please enter all valid input fields");
			return new ResponseEntity<>(null, HttpStatusCode.valueOf(400));
		}
		String firstName = requMap.get("first_name");
		String lastName = requMap.get("last_name");
		String username = requMap.get("username");
		String password = requMap.get("password");

		if (Utils.isOnlyText(firstName) == false || Utils.isOnlyText(lastName) == false
				|| Utils.isValidString(password) == false || Utils.isEmailValidated(username) == false) {
			resMap.clear();
			resMap.put("msg", "Please enter valid input");
			return new ResponseEntity<>(null, HttpStatusCode.valueOf(400));
		}

//		Optional<User> userExists = userRepository.findByUsername(username);
//		if (userExists.isPresent()) {
//			resMap.clear();
//			resMap.put("msg", "Email already exists");
//			return new ResponseEntity<>(null, HttpStatusCode.valueOf(400));
//		}

		User user = new User();
		VerificationInfo verificationInfo = new VerificationInfo();
		user.setId(UUID.randomUUID());
		verificationInfo.setId(user.getId());
		verificationInfo.setUsername(username);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setPassword(authHandler.hash(password));
		user.setAccountCreated(new Date());
		user.setAccountUpdated(new Date());
		user.setVerified(false);
		userRepository.save(user);

		try {
			String messageStr = objectMapper.writeValueAsString(user);
			ByteString data = ByteString.copyFromUtf8(messageStr);
			PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

			// Publish the message
			ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
			messageIdFuture.addListener(() -> {
				try {
					// Log the messageId, which can be used for tracking and debugging
					System.out.println("Published message ID: " + messageIdFuture.get());
				} catch (InterruptedException | ExecutionException e) {
					System.err.println("Error publishing message to Pub/Sub: " + e.getMessage());
				}
			}, Executors.newSingleThreadExecutor());

		} catch (JsonProcessingException e) {
			// Handle JSON serialization error
			System.err.println("Error serializing user data to JSON: " + e.getMessage());
		}

		resMap.clear();
		//resMap.put("id", user.getId());
		resMap.put("id", user.getId().toString());
		resMap.put("first_name", user.getFirstName());
		resMap.put("last_name", user.getLastName());
		resMap.put("username", user.getUsername());
		resMap.put("account_created", user.getAccountCreated());
		resMap.put("account_updated", user.getAccountUpdated());

		return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(201));
	}

	/*public ResponseEntity<Map<String, Object>> fetchById(String userId, HttpServletRequest httpServletRequest) {
		Map<String, Object> resMap = new HashMap<>();
		if (Utils.isValidString(userId) == false) {
			resMap.clear();
			resMap.put("msg", "Please enter input id");
			return new ResponseEntity<>(null, HttpStatusCode.valueOf(400));
		}

		if (Utils.isOnlyNumber(userId.trim()) == false) {
			resMap.clear();
			resMap.put("msg", "Please enter valid id integer");
			return new ResponseEntity<>(null, HttpStatusCode.valueOf(400));
		}
		int givenUserId = Integer.parseInt(userId);
		//UUID givenUserId = userId;

		User authUser = authHandler.getUser(httpServletRequest);
		if (authUser == null) {
			resMap.clear();
			resMap.put("msg", "Please enter valid credentials");
			return new ResponseEntity<>(null, HttpStatusCode.valueOf(401));
		}

		if (givenUserId != authUser.getId()) {
			resMap.clear();
			resMap.put("msg", "Forbidden to view the data");
			return new ResponseEntity<>(null, HttpStatusCode.valueOf(403));
		}

		resMap.clear();
		User user = authUser;
		resMap.put("id", user.getId());
		resMap.put("first_name", user.getFirstName());
		resMap.put("last_name", user.getLastName());
		resMap.put("username", user.getUsername());
		resMap.put("account_created", user.getAccountCreated());
		resMap.put("account_updated", user.getAccountUpdated());
		return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(200));
	} */
	//UUID CODE FIRST DRAFT
	/*public ResponseEntity<Map<String, Object>> fetchById(String userId, HttpServletRequest httpServletRequest) {
		Map<String, Object> resMap = new HashMap<>();
//UUID CODE FIRST DRAFT
		if (!Utils.isValidString(userId)) {
			resMap.put("msg", "Please enter input id");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400));
		}

		try {
			UUID givenUserId = UUID.fromString(userId.trim()); // Convert the String to UUID

			User authUser = authHandler.getUser(httpServletRequest);
			if (authUser == null) {
				resMap.put("msg", "Please enter valid credentials");
				return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(401));
			}

			if (!givenUserId.equals(authUser.getId())) { // Compare UUIDs
				resMap.put("msg", "Forbidden to view the data");
				return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(403));
			}

			User user = authUser; // Or fetch the user from the repository using the UUID
			resMap.put("id", user.getId().toString()); // Convert UUID to String when sending the response
			resMap.put("first_name", user.getFirstName());
			resMap.put("last_name", user.getLastName());
			resMap.put("username", user.getUsername());
			resMap.put("account_created", user.getAccountCreated());
			resMap.put("account_updated", user.getAccountUpdated());
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(200));
		} catch (IllegalArgumentException e) {
			resMap.put("msg", "Please enter a valid UUID");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400));
		}
	} */

	public ResponseEntity<Map<String, Object>> fetchById(String userId, HttpServletRequest httpServletRequest) {
		Map<String, Object> resMap = new HashMap<>();
		if (!Utils.isValidString(userId)) {
			resMap.put("msg", "Please enter input id");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400));
		}

		UUID givenUserId;
		try {
			givenUserId = UUID.fromString(userId.trim());
		} catch (IllegalArgumentException e) {
			resMap.put("msg", "Please enter a valid UUID");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400));
		}

		User authUser = authHandler.getUser(httpServletRequest);
		if (authUser == null) {
			resMap.put("msg", "Please enter valid credentials");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(401));
		}

		if (!givenUserId.equals(authUser.getId())) {
			resMap.put("msg", "Forbidden to view the data");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(403));
		}

		// Assuming the user to fetch is the authenticated user
		// If you need to fetch another user by UUID, use userRepository.findById(givenUserId)
		User user = authUser;

		// Convert UUID to string for the response
		resMap.put("id", user.getId().toString());
		resMap.put("first_name", user.getFirstName());
		resMap.put("last_name", user.getLastName());
		resMap.put("username", user.getUsername());
		resMap.put("account_created", user.getAccountCreated());
		resMap.put("account_updated", user.getAccountUpdated());
		return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(200));
	}




	private void addDataToResponse(Map<String, Object> resMap, String username) {

	}

	/*public ResponseEntity<Map<String, Object>> updateUserById(String userId, Map<String, String> requMap,
			HttpServletRequest httpServletRequest) {
		Map<String, Object> resMap = new HashMap<>();
		HashSet<String> set = new HashSet<>();
		set.add("first_name");
		set.add("last_name");
		set.add("password");
		int c = 0;

		for (String str : requMap.keySet()) {
			if (set.contains(str) == false) {
				resMap.clear();
				resMap.put("msg", "Only limited fields are alllowed to update");
				return new ResponseEntity<>(null, HttpStatusCode.valueOf(400));
			}
			c++;
		}
		if (c == 0) {
			resMap.clear();
			resMap.put("msg", "No fields to update");
			return new ResponseEntity<>(null, HttpStatusCode.valueOf(400));
		}

		if (Utils.isValidString(userId) == false) {
			resMap.clear();
			resMap.put("msg", "Please enter input id");
			return new ResponseEntity<>(null, HttpStatusCode.valueOf(400));
		}

		if (Utils.isOnlyNumber(userId.trim()) == false) {
			resMap.clear();
			resMap.put("msg", "Please enter valid id integer");
			return new ResponseEntity<>(null, HttpStatusCode.valueOf(400));
		}
		int givenUserId = Integer.parseInt(userId);

		User authUser = authHandler.getUser(httpServletRequest);
		if (authUser == null) {
			resMap.clear();
			resMap.put("msg", "Please enter valid credentials");
			return new ResponseEntity<>(null, HttpStatusCode.valueOf(401));
		}

		if (givenUserId != authUser.getId()) {
			resMap.clear();
			resMap.put("msg", "Forbidden to view the data");
			return new ResponseEntity<>(null, HttpStatusCode.valueOf(403));
		}

		User user = authUser;

		String firstName = requMap.getOrDefault("first_name", null);
		String lastName = requMap.getOrDefault("last_name", null);
		String password = requMap.getOrDefault("password", null);

		if (Utils.isOnlyText(firstName)) {
			user.setFirstName(firstName);
		}

		if (Utils.isOnlyText(lastName)) {
			user.setLastName(lastName);
		}

		if (Utils.isValidString(password)) {
			user.setPassword(authHandler.hash(password));
		}

		user.setAccountUpdated(new Date());
		userRepository.save(user);
		return new ResponseEntity<>(null, HttpStatusCode.valueOf(204));
	}*/

	public ResponseEntity<Map<String, Object>> updateUserById(String userId, Map<String, String> requMap,
															  HttpServletRequest httpServletRequest) {
		Map<String, Object> resMap = new HashMap<>();
		HashSet<String> set = new HashSet<>();
		set.add("first_name");
		set.add("last_name");
		set.add("password");

		// Ensure that only the allowed fields are present in the request map
		if (!requMap.keySet().stream().allMatch(set::contains)) {
			resMap.put("msg", "Only limited fields are allowed to update");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400));
		}
		if (requMap.isEmpty()) {
			resMap.put("msg", "No fields to update");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400));
		}

		// Validate the UUID string
		if (!Utils.isValidString(userId)) {
			resMap.put("msg", "Please enter input id");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400));
		}
		UUID givenUserId;
		try {
			givenUserId = UUID.fromString(userId.trim());
		} catch (IllegalArgumentException e) {
			resMap.put("msg", "Please enter a valid UUID");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400));
		}

		// Authenticate the user
		User authUser = authHandler.getUser(httpServletRequest);
		if (authUser == null) {
			resMap.put("msg", "Please enter valid credentials");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(401));
		}

		// Authorization check: make sure the authenticated user's ID matches the given user ID
		if (!givenUserId.equals(authUser.getId())) {
			resMap.put("msg", "Forbidden to view the data");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(403));
		}

		// Update the user fields
		User user = userRepository.findById(givenUserId).orElse(null);
		if (user == null) {
			resMap.put("msg", "User not found");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(404));
		}

		String firstName = requMap.getOrDefault("first_name", user.getFirstName());
		String lastName = requMap.getOrDefault("last_name", user.getLastName());
		String password = requMap.get("password"); // No default value for password

		if (Utils.isOnlyText(firstName)) {
			user.setFirstName(firstName);
		}

		if (Utils.isOnlyText(lastName)) {
			user.setLastName(lastName);
		}

		if (password != null && Utils.isValidString(password)) {
			user.setPassword(authHandler.hash(password));
		}

		user.setAccountUpdated(new Date());
		userRepository.save(user);

		return new ResponseEntity<>(null, HttpStatusCode.valueOf(204));
	}

}
