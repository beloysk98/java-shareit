package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ErrorHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        log.warn("400: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Ошибка валидации");
        log.warn("400: {}", message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotReadable(final HttpMessageNotReadableException e) {
        log.warn("400: некорректное тело запроса");
        return new ErrorResponse("Некорректное тело запроса");
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ErrorResponse> handleHttpStatus(final HttpStatusCodeException e) {
        log.warn("{}: {}", e.getStatusCode(), e.getResponseBodyAsString());
        String message = extractErrorMessage(e.getResponseBodyAsString());
        return ResponseEntity.status(e.getStatusCode())
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOther(final Exception e) {
        log.error("500: {}", e.getMessage(), e);
        return new ErrorResponse("Внутренняя ошибка сервера");
    }

    private String extractErrorMessage(String body) {
        try {
            JsonNode node = objectMapper.readTree(body);
            if (node.has("error")) {
                return node.get("error").asText();
            }
            return body;
        } catch (Exception ex) {
            return body;
        }
    }
}