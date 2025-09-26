package com.example.restaurant_management.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private @Builder.Default Boolean success = Boolean.FALSE;

    private String code;
    private T data;

    public static <V> ResponseEntity<RestaurantResponse<V>> ok(V model) {
        RestaurantResponse<V> response = new RestaurantResponse<>();
        response.success = true;
        response.data = model;
        return ResponseEntity.ok(response);
    }
}