package community.mingle.api.global.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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
        if (httpStatus == null) httpStatus = INTERNAL_SERVER_ERROR;
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
        return new ResponseEntity<>(errorResponse, resHeaders, BAD_REQUEST);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException e) {
        HttpHeaders resHeaders = new HttpHeaders();
        resHeaders.add("Content-Type", "application/json;charset=UTF-8");

        String messageCode = "RUNTIME_ERROR";
        String errorMessage = e.getMessage(); //TODO exception log 고려 필요
        ErrorResponse errorResponse = getErrorResponse(messageCode, errorMessage);
        return new ResponseEntity<>(errorResponse, resHeaders, INTERNAL_SERVER_ERROR);
    }

    public ErrorResponse getErrorResponse(String originCode, String message) {
        return switch (originCode) {
            case "NotBlank" -> new ErrorResponse(BAD_REQUEST.value(), "1001", message);
            case "Email", "Pattern" -> new ErrorResponse(BAD_REQUEST.value(), "1002", message);
            case "Max", "Min", "Size" -> new ErrorResponse(BAD_REQUEST.value(), "1003", message);
            default -> new ErrorResponse(INTERNAL_SERVER_ERROR.value(), "1000", message);
        };
    }
}