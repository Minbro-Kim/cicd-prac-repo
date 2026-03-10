package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorResponseTest {

  @Test
  void of_BindingResult_ShouldMapCorrectly() {
    // Arrange
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError("objectName", "email", "invalid email");
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

    // Act
    ErrorResponse response = ErrorResponse.of(bindingResult);

    // Assert
    assertNotNull(response.getFieldErrors());
    assertEquals("email", response.getFieldErrors().get(0).getField());
    assertEquals("invalid email", response.getFieldErrors().get(0).getMessage());
  }

  @Test
  void of_CodeAndMessage_ShouldCreateCorrectResponse() {
    // Act
    ErrorResponse response = ErrorResponse.of(404, "Not Found");

    // Assert
    assertEquals(404, response.getCode());
    assertEquals("Not Found", response.getMessage());
  }

  @Test
  void of_ConstraintViolations_ShouldMapCorrectly() {
    // Arrange
    Set<ConstraintViolation<?>> violations = Collections.emptySet();

    // Act
    ErrorResponse response = ErrorResponse.of(violations);

    // Assert
    assertNotNull(response.getViolationErrors());
    assertTrue(response.getViolationErrors().isEmpty());
  }
}