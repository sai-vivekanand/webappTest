package com.neu.cloud.cloudApp.controller;

import java.util.HashMap;
import java.util.Map;

import com.neu.cloud.cloudApp.Utils.AuthHandler;
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

	@Autowired
	private UserService userService;

	@Autowired
	private AuthHandler authHandler;

	@PostMapping("/v1/user")
	public ResponseEntity<Map<String, Object>> createUser(@RequestBody(required = false) Map<String, String> requMap) {
		try {
			return userService.save(requMap);
		} catch (Exception e) {
			Map<String, Object> resMap = new HashMap<>();
			resMap.put("msg", "Please enter valid input");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400));
		}
	}

	@GetMapping("/v1/user/self")
	public ResponseEntity<Map<String, Object>> getAuthenticatedUser(HttpServletRequest httpServletRequest) {
		// Check if there's any payload in the GET request
		if (httpServletRequest.getContentLengthLong() > 0 || httpServletRequest.getHeader("Transfer-Encoding") != null) {
			Map<String, Object> resMap = new HashMap<>();
			resMap.put("msg", "Bad request. GET request should not contain a payload.");
			return new ResponseEntity<>(resMap, HttpStatusCode.valueOf(400)); // Assuming HttpStatusCode supports valueOf like HttpStatus
		}

		try {
			// The AuthHandler is assumed to extract the authenticated user's details
			User authUser = authHandler.getUser(httpServletRequest);
			if (authUser == null) {
				Map<String, Object> resMap = new HashMap<>();
				resMap.put("msg", "Unauthorized access. Please provide valid credentials.");
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
