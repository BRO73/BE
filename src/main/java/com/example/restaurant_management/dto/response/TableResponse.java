package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.entity.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {
    private Long id;
    private String tableNumber;
    private Integer capacity;
    private String location;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    private boolean activated;

    public static TableResponse fromEntity(Table table) {
        return TableResponse.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .location(table.getLocation())
                .status(table.getStatus())
                .createdAt(table.getCreatedAt())
                .updatedAt(table.getUpdatedAt())
                .deleted(table.isDeleted())
                .activated(table.isActivated())
                .build();
    }
}
