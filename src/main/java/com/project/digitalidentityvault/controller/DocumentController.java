package com.project.digitalidentityvault.controller;

import com.project.digitalidentityvault.dto.DocumentDto;
import com.project.digitalidentityvault.entity.Document;
import com.project.digitalidentityvault.exception.FileUploadException;
import com.project.digitalidentityvault.service.DocumentService;
import com.project.digitalidentityvault.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/documents")
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<String> saveDocument(@ModelAttribute DocumentDto request,
                                               @RequestHeader("Authorization") String token)
            throws UsernameNotFoundException, IOException, FileUploadException {
        documentService.saveDocument(request, token);
        return ResponseEntity.ok(Constants.DOCUMENT_UPLOAD);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestBody String email, @RequestBody String otp, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(documentService.verifyOtp(email, otp, token));
    }

    @GetMapping("/view")
    public ResponseEntity<List<DocumentDto>> viewDocuments(@RequestParam String email,
                                                        @RequestHeader("Authorization") String token) throws UsernameNotFoundException {
        return ResponseEntity.ok(documentService.viewDocuments(email, token));
    }
}
