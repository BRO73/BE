package com.example.demo_innocode.dto.request;


import lombok.Data;

@Data
public class MediaRequestDTO {
    private String filePath;
    private boolean header;
    private String fileType;
    private String description;
    private Long locationId;
}

