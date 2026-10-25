package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T> ResponseEntity<T> get(String path, Class<T> responseType) {
        return get(path, null, null, responseType);
    }

    protected <T> ResponseEntity<T> get(String path, Long userId, Class<T> responseType) {
        return get(path, userId, null, responseType);
    }

    protected <T> ResponseEntity<T> get(String path, Long userId, Map<String, Object> parameters, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null, responseType);
    }

    @SuppressFBWarnings
    protected <T, R> ResponseEntity<R> post(String path, Long userId, T body, Class<R> responseType) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, null, body, responseType);
    }

    protected <T, R> ResponseEntity<R> patch(String path, Long userId, T body, Class<R> responseType) {
        return patch(path, userId, null, body, responseType);
    }

    protected <T, R> ResponseEntity<R> patch(String path, Long userId, Map<String, Object> parameters, T body, Class<R> responseType) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body, responseType);
    }

    @SuppressFBWarnings
    protected <T> ResponseEntity<T> delete(String path, Long userId, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, null, null, responseType);
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                        Map<String, Object> parameters, T body, Class<R> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.add("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<T> requestEntity = new HttpEntity<>(body, headers);
        return rest.exchange(path, method, requestEntity, responseType, parameters);
    }
}