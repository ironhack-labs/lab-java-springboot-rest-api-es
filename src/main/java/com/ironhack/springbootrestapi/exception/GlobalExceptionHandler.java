package com.ironhack.springbootrestapi.exception;

import com.ironhack.springbootrestapi.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Turns every exception thrown while handling a request into an {@link ApiError}
 * JSON body with a meaningful HTTP status code.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String API_KEY_HEADER = "API-Key";

    /**
     * A {@code @Valid} request body broke one or more constraints on
     * {@code Product} or {@code Customer}.
     *
     * @return 400 Bad Request with the message of every invalid field.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.merge(fieldError.getField(), fieldError.getDefaultMessage(),
                    (first, second) -> first + " " + second);
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed.", request, fieldErrors);
    }

    /**
     * A required header is absent. For {@code API-Key} that means the caller is
     * not authenticated (401); any other header would be a plain bad request.
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiError> handleMissingHeader(MissingRequestHeaderException exception,
            HttpServletRequest request) {
        if (API_KEY_HEADER.equalsIgnoreCase(exception.getHeaderName())) {
            return buildResponse(HttpStatus.UNAUTHORIZED, "The API-Key header is required.", request);
        }
        String message = "The '%s' header is required.".formatted(exception.getHeaderName());
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    /** @return 401 Unauthorized when the API-Key header has the wrong value. */
    @ExceptionHandler(InvalidApiKeyException.class)
    public ResponseEntity<ApiError> handleInvalidApiKey(InvalidApiKeyException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
    }

    /** @return 404 Not Found for an unknown product name or customer email. */
    @ExceptionHandler({ProductNotFoundException.class, CustomerNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    /** @return 409 Conflict when a name or email is already taken. */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    /** @return 400 Bad Request for a negative bound or {@code min > max}. */
    @ExceptionHandler(InvalidPriceRangeException.class)
    public ResponseEntity<ApiError> handleInvalidPriceRange(InvalidPriceRangeException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    /**
     * A required query parameter is absent, for example
     * {@code /products/price?min=10} without {@code max}.
     *
     * @return 400 Bad Request naming the missing parameter.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(MissingServletRequestParameterException exception,
            HttpServletRequest request) {
        String message = "The '%s' query parameter is required.".formatted(exception.getParameterName());
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    /**
     * A path variable or query parameter could not be converted to the expected
     * type, for example {@code /products/price?min=abc&max=10}.
     *
     * @return 400 Bad Request naming the offending parameter and value.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {
        String message = "'%s' is not a valid value for '%s'.".formatted(exception.getValue(), exception.getName());
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    /**
     * The body is missing, is not JSON, or has a value of the wrong type such as
     * {@code "price": "cheap"}.
     *
     * @return 400 Bad Request.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "The request body is missing or is not valid JSON for this endpoint.", request);
    }

    /**
     * Safety net for everything else. Spring's own web exceptions (unknown URL,
     * wrong HTTP method, unsupported content type...) implement
     * {@link ErrorResponse} and already know their status, so they keep it
     * instead of being reported as 500. Anything else is a real server error: the
     * cause is logged and the client only gets a generic message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(Exception exception, HttpServletRequest request) {
        if (exception instanceof ErrorResponse errorResponse) {
            String detail = errorResponse.getBody().getDetail();
            return buildResponse(errorResponse.getStatusCode(), detail, request);
        }
        logger.error("Unexpected error while handling {}", request.getRequestURI(), exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", request);
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatusCode status, String message, HttpServletRequest request) {
        return buildResponse(status, message, request, Map.of());
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatusCode status, String message, HttpServletRequest request,
            Map<String, String> fieldErrors) {
        HttpStatus resolved = HttpStatus.resolve(status.value());
        String reason = resolved != null ? resolved.getReasonPhrase() : "";
        ApiError body = new ApiError(
                Instant.now().toString(),
                status.value(),
                reason,
                message,
                request.getRequestURI(),
                fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}
