package com.project.digitalidentityvault.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
public class DocumentDto implements Serializable {
    private String email;
    private String type;
    private MultipartFile file;

    public DocumentDto(String type, String filePath) {
    }
}