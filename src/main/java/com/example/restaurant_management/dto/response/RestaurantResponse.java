package com.example.restaurant_management.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantResponse<T> {

    private Integer httpStatus;
    private String message;
    @Builder.Default
    private Boolean success = Boolean.FALSE;
    private String code;
    private T data;


    public static <V> ResponseEntity<RestaurantResponse<V>> ok(V model) {
        RestaurantResponse<V> response = new RestaurantResponse<>();
        response.httpStatus = HttpStatus.OK.value();
        response.message = "Success";
        response.success = true;
        response.code = HttpStatus.OK.getReasonPhrase();
        response.data = model;
        return ResponseEntity.ok(response);
    }


    public static <V> ResponseEntity<RestaurantResponse<V>> ok(V model, String message) {
        RestaurantResponse<V> response = new RestaurantResponse<>();
        response.httpStatus = HttpStatus.OK.value();
        response.message = message;
        response.success = true;
        response.code = HttpStatus.OK.getReasonPhrase();
        response.data = model;
        return ResponseEntity.ok(response);
    }


    public static <V> ResponseEntity<RestaurantResponse<V>> error(HttpStatus status, String message, String code) {
        RestaurantResponse<V> response = new RestaurantResponse<>();
        response.httpStatus = status.value();
        response.message = message;
        response.success = false;
        response.code = code != null ? code : status.getReasonPhrase();
        return ResponseEntity.status(status).body(response);
    }
}
