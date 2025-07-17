package com.example.demo_innocode.dto.response;


import lombok.Data;
@Data
public class MediaResponseDTO {
    private Long id;
    private String filePath;
    private boolean header;
    private String fileType;
    private String description;
    private Long locationId;
    private String locationName; // nếu muốn trả thêm tên location
}
