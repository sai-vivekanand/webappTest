package com.neu.cloud.cloudApp.controller;

import java.util.HashMap;
import java.util.Map;

import com.neu.cloud.cloudApp.Utils.AuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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


}
