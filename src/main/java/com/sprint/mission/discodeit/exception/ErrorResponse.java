package com.sprint.mission.discodeit.exception;


import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ErrorResponse{
    private List<FieldError> fieldErrors;
    private List<ConstraintViolationError> violationErrors;
    private Integer code;
    private String message;

    private ErrorResponse(List<FieldError> fieldErrors,List<ConstraintViolationError> violationErrors){
        this.fieldErrors = fieldErrors;
        this.violationErrors = violationErrors;
    }
    private ErrorResponse(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse of(BindingResult bindingResult){
        return new ErrorResponse(FieldError.of(bindingResult),null);
    }

    public static ErrorResponse of(Set<ConstraintViolation<?>> violations){
        return new ErrorResponse(null, ConstraintViolationError.of(violations));
    }

    public static ErrorResponse of(Integer code, String message){
        return new ErrorResponse(code, message);
    }
    //내부 dto
    @Getter
    public static class FieldError{
        private String field;
        private Object rejectedValue;
        private String message;

        private FieldError(String field, Object rejectedValue, String message){
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }
        public static List<FieldError> of(BindingResult bindingResult){
            return bindingResult.getFieldErrors().stream()
                    .map(e -> new FieldError(
                                e.getField(),
                            e.getRejectedValue() == null ? null:e.getRejectedValue().toString(),
                            e.getDefaultMessage()
                    )).collect(Collectors.toList());
        }
    }
    @Getter
    public static class ConstraintViolationError{
        private String propertyPath;
        private Object rejectedValue;
        private String message;
        private ConstraintViolationError(String propertyPath, Object rejectedValue, String message){
            this.propertyPath = propertyPath;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }
        private static List<ConstraintViolationError> of(Set<ConstraintViolation<?>> violations){
            return violations.stream()
                    .map(v->new ConstraintViolationError(
                            v.getPropertyPath().toString(),
                            v.getInvalidValue(),
                            v.getMessage()
                    )).collect(Collectors.toList());
        }
    }
}
