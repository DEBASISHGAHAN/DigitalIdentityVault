package com.project.digitalidentityvault.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {
    private String email;
    private String type;
    private MultipartFile file;

    public DocumentDto(String type, String filePath) {
    }
}

