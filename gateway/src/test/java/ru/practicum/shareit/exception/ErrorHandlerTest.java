package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ErrorHandlerTest {

    private ErrorHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ErrorHandler(new ObjectMapper());
    }

    @Test
    void handleValidation_shouldReturn400() {
        ErrorResponse r = handler.handleValidation(new ValidationException("bad"));
        assertThat(r.getError()).isEqualTo("bad");
    }

    @Test
    void handleMethodArgumentNotValid_withFieldError_shouldReturn400() {
        MethodArgumentNotValidException e = mock(MethodArgumentNotValidException.class);
        BindingResult br = mock(BindingResult.class);
        when(e.getBindingResult()).thenReturn(br);
        when(br.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "field", "msg")));

        ErrorResponse r = handler.handleMethodArgumentNotValid(e);
        assertThat(r.getError()).contains("field").contains("msg");
    }

    @Test
    void handleMethodArgumentNotValid_withoutFieldError_shouldReturn400() {
        MethodArgumentNotValidException e = mock(MethodArgumentNotValidException.class);
        BindingResult br = mock(BindingResult.class);
        when(e.getBindingResult()).thenReturn(br);
        when(br.getFieldErrors()).thenReturn(List.of());

        ErrorResponse r = handler.handleMethodArgumentNotValid(e);
        assertThat(r.getError()).isEqualTo("Ошибка валидации");
    }

    @Test
    void handleNotReadable_shouldReturn400() {
        ErrorResponse r = handler.handleNotReadable(mock(HttpMessageNotReadableException.class));
        assertThat(r.getError()).isEqualTo("Некорректное тело запроса");
    }

    @Test
    void handleHttpStatus_withJsonBody_shouldExtractError() {
        HttpStatusCodeException e = mock(HttpStatusCodeException.class);
        when(e.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
        when(e.getResponseBodyAsString()).thenReturn("{\"error\":\"not found\"}");

        ResponseEntity<ErrorResponse> resp = handler.handleHttpStatus(e);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody().getError()).isEqualTo("not found");
    }

    @Test
    void handleHttpStatus_withNonJsonBody_shouldReturnRawBody() {
        HttpStatusCodeException e = mock(HttpStatusCodeException.class);
        when(e.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(e.getResponseBodyAsString()).thenReturn("plain text");

        ResponseEntity<ErrorResponse> resp = handler.handleHttpStatus(e);
        assertThat(resp.getBody().getError()).isEqualTo("plain text");
    }

    @Test
    void handleOther_shouldReturn500() {
        ErrorResponse r = handler.handleOther(new RuntimeException("boom"));
        assertThat(r.getError()).isEqualTo("Внутренняя ошибка сервера");
    }
}