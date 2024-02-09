package com.neu.cloud.cloudApp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import com.neu.cloud.cloudApp.controller.healthCheckController;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class HealthCheckControllerTests {

	@Test
	void healthCheck_ReturnsOk_WhenConnectionSuccessful() throws SQLException {
		// Arrange
		DataSource mockDataSource = mock(DataSource.class);
		Connection mockConnection = mock(Connection.class);
		when(mockDataSource.getConnection()).thenReturn(mockConnection);

		healthCheckController controller = new healthCheckController(mockDataSource);
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getMethod()).thenReturn("GET");
		when(mockRequest.getContentLengthLong()).thenReturn(0L);

		// Act
		ResponseEntity<Void> response = controller.healthCheck(mockRequest);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void healthCheck_ReturnsServiceUnavailable_WhenConnectionFails() throws SQLException {
		// Arrange
		DataSource mockDataSource = mock(DataSource.class);
		when(mockDataSource.getConnection()).thenThrow(new SQLException());

		healthCheckController controller = new healthCheckController(mockDataSource);
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getMethod()).thenReturn("GET");
		when(mockRequest.getContentLengthLong()).thenReturn(0L);

		// Act
		ResponseEntity<Void> response = controller.healthCheck(mockRequest);

		// Assert
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
	}

	@Test
	void healthCheck_ReturnsBadRequest_WhenBodyInGetRequest() {
		// Arrange
		DataSource mockDataSource = mock(DataSource.class); // This won't be used in this test case
		healthCheckController controller = new healthCheckController(mockDataSource);
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getMethod()).thenReturn("GET");
		when(mockRequest.getContentLengthLong()).thenReturn(100L); // Simulate body in GET request

		// Act
		ResponseEntity<Void> response = controller.healthCheck(mockRequest);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
}
