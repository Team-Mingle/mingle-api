package community.mingle.api.global.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> customExceptionHandler(CustomException customException) {
        HttpHeaders resHeaders = new HttpHeaders();
        resHeaders.add("Content-Type", "application/json;charset=UTF-8");

        ErrorCode errorCode = customException.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        HttpStatus httpStatus = HttpStatus.resolve(errorCode.getStatus());
        if (httpStatus == null) httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(errorResponse, resHeaders, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        HttpHeaders resHeaders = new HttpHeaders();
        resHeaders.add("Content-Type", "application/json;charset=UTF-8");

        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "";
        String messageCode = fieldError != null && fieldError.getCode() != null ? fieldError.getCode() : "";
        ErrorResponse errorResponse = getErrorResponse(messageCode, errorMessage);
        return new ResponseEntity<>(errorResponse, resHeaders, HttpStatus.BAD_REQUEST);
    }

    public ErrorResponse getErrorResponse(String originCode, String message) {
        return switch (originCode) {
            case "NotBlank" -> new ErrorResponse("1001", message);
            case "Email", "Pattern" -> new ErrorResponse("1002", message);
            case "Max", "Min", "Size" -> new ErrorResponse("1003", message);
            default -> new ErrorResponse("1000", message);
        };
    }
}