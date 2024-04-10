package com.neu.cloud.cloudApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
public class healthCheckController {

    private static final Logger logger = LoggerFactory.getLogger(healthCheckController.class);
    private DataSource dataSource;

    @Autowired
    public healthCheckController(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @GetMapping("/healthz")
    public ResponseEntity<Void> healthCheck(HttpServletRequest request) {
        // Check if there's a content-length header indicating a body in a GET request
        logger.info("This is NEW a healthz structured log message to check INFO");
        logger.error("This is NEW a healthz structured log message to check ERROR");
        logger.debug("This is NEW a healthz structured log message to check DEBUG");
        if ("GET".equals(request.getMethod()) && request.getContentLengthLong() > 0) {
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
}

