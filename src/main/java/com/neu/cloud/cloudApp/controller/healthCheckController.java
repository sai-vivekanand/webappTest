package com.neu.cloud.cloudApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
public class healthCheckController {

    private DataSource dataSource;

    @Autowired
    public healthCheckController(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @GetMapping("/healthz")
    public ResponseEntity<Void> healthCheck(HttpServletRequest request) {
        // Explicitly block non-GET requests
        if (!"GET".equals(request.getMethod())) {
            // Return a 405 Method Not Allowed response
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        // Check if there's a content-length header indicating a body in a GET request
        if (request.getContentLengthLong() > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try (Connection conn = dataSource.getConnection()) {
            // Successfully obtained a connection
            return ResponseEntity.ok().header("Cache-Control", "no-cache, no-store, must-revalidate").build();
        } catch (SQLException e) {
            // Failed to obtain a connection
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("Cache-Control", "no-cache, no-store, must-revalidate").build();
        }
    }
    @RequestMapping(path = "/healthz", method = {RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<Void> disallowNonGetRequests() {
        // Explicitly return a 405 Method Not Allowed for non-GET requests, including OPTIONS
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
}

