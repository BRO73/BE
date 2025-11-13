package com.example.restaurant_management.dto.response;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FloorElementResponse {
    private String id;
    private String type;
    private double x;
    private double y;
    private double width;
    private double height;
    private double rotation;
    private String color;
    private String label;
    private String floor;
    private Long tableId;
}