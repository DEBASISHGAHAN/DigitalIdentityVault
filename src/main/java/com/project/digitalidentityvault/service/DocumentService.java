package com.project.digitalidentityvault.service;

import com.project.digitalidentityvault.dto.DocumentDto;
import com.project.digitalidentityvault.dto.UserDto;
import com.project.digitalidentityvault.entity.Document;
import com.project.digitalidentityvault.entity.User;
import com.project.digitalidentityvault.exception.FileUploadException;
import com.project.digitalidentityvault.exception.UserException;
import com.project.digitalidentityvault.repository.DocumentRepository;
import com.project.digitalidentityvault.repository.UserRepository;
import com.project.digitalidentityvault.util.Constants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.project.digitalidentityvault.util.Validation.isValidFileExtension;
import static com.project.digitalidentityvault.util.Validation.validateEmail;

@Service
@Getter
@Slf4j
@RequiredArgsConstructor
public class DocumentService {
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final MailService mailService;
    private final OtpService otpService;
    private final RedisService redisService;
    private final UserService userService;
    private final OtpRateLimiterService otpRateLimiterService;

    @Value("${document.basePath}")
    public String documentPath;

    public void saveDocument(DocumentDto request, String token) throws IOException, FileUploadException {
        log.info("Saving document for user...");
        // Validate Session
        validateSession(request.getEmail(), token);
        // Validate Request
        User user = validateDocumentRequest(request);
        // Generate and Save File
        String filePath = generateFilePath(user, request);
        request.getFile().transferTo(Path.of(filePath));
        log.info("Document saved at: {}", filePath);
        // Save Document in Database
        Document document = Document.builder()
                .user(user)
                .type(request.getType())
                .filePath(filePath)
                .uploadedAt(LocalDateTime.now())
                .build();
        documentRepository.save(document);
    }

    public List<DocumentDto> viewDocuments(UserDto user, String token) throws UsernameNotFoundException {
        log.info("Fetching documents for user: {}", user.getEmail());
        // Validate Session
        validateSession(user.getEmail(), token);
        if (!otpService.validateOtp(user.getEmail(), user.getOtp())){
            throw new UserException(Constants.INVALID_OTP);
        }
        List<Document> documents = documentRepository.findByUserId(user.getUserId());

        return documents.stream()
                .map(doc -> new DocumentDto(
                        doc.getType(),
                        doc.getFilePath()))
                .toList();
    }

    public String sendViewDocumentOtp(String email, String token) {
        validateSession(email, token);
        if (!otpRateLimiterService.isOtpRequestAllowed(email)) {
            throw new UserException(Constants.OTP_REQUEST_EXCEED);
        }
        String otp = otpService.generateOtp(email);
        log.info("Generated OTP for {}: {}", email, otp);
        mailService.sendOtpMail(email, "OTP Verification", otp);
        return Constants.SEND_OTP;
    }

    private User validateDocumentRequest(DocumentDto request) throws FileNotFoundException, FileUploadException {
        // Validate Email
        validateEmail(request.getEmail());
        // Validate User Existence
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USER_NOT_FOUND));
        // Validate File
        if (request.getFile().isEmpty()) {
            throw new FileNotFoundException(Constants.FILE_EMPTY);
        }
        // Validate File Size
        if (request.getFile().getSize() > Constants.DOCUMENT_UPLOAD_MAX_SIZE_MB) {
            throw new FileUploadException(Constants.FILE_UPLOAD_SIZE_EXCEEDS);
        }
        // Validate File Extension
        String fileName = request.getFile().getOriginalFilename();
        if (fileName == null || !isValidFileExtension(fileName)) {
            throw new FileUploadException(Constants.INVALID_FILE_FORMAT);
        }
        return user;
    }

    private String generateFilePath(User user, DocumentDto request) {
        return documentPath + user.getId() + "_" + request.getType() + "_"
                + System.currentTimeMillis() + "_" + request.getFile().getOriginalFilename();
    }

    private void validateSession(String email, String token) {
        String session = redisService.getSession(email);
        if (session.isEmpty() || !session.equals(token)) {
            throw new UserException(Constants.INVALID_SESSION);
        }
    }
}
