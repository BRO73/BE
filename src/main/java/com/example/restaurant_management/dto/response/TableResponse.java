package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.entity.TableEntity;
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

    public static TableResponse fromEntity(TableEntity tableEntity) {
        return TableResponse.builder()
                .id(tableEntity.getId())
                .tableNumber(tableEntity.getTableNumber())
                .capacity(tableEntity.getCapacity())
                .location(tableEntity.getLocation())
                .status(tableEntity.getStatus())
                .createdAt(tableEntity.getCreatedAt())
                .updatedAt(tableEntity.getUpdatedAt())
                .deleted(tableEntity.isDeleted())
                .activated(tableEntity.isActivated())
                .build();
    }
}
