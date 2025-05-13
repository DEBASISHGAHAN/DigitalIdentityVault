package com.project.digitalidentityvault.service;

import com.project.digitalidentityvault.dto.DocumentDto;
import com.project.digitalidentityvault.dto.UserDto;
import com.project.digitalidentityvault.entity.Document;
import com.project.digitalidentityvault.entity.User;
import com.project.digitalidentityvault.exception.UserException;
import com.project.digitalidentityvault.repository.DocumentRepository;
import com.project.digitalidentityvault.repository.UserRepository;
import com.project.digitalidentityvault.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private MailService mailService;
    @Mock
    private OtpService otpService;
    @Mock
    private RedisService redisService;
    @Mock
    private OtpRateLimiterService otpRateLimiterService;
    @InjectMocks
    private DocumentService documentService;

    private User testUser;
    private DocumentDto documentDto;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).email("test@gmail.com").verified(true).build();
        documentDto = new DocumentDto();
        documentDto.setEmail("test@gmail.com");
        documentDto.setType("Passport");
        jwtToken = "valid-jwt-token";
        documentService.documentPath = "/uploads/";
    }

    @Test
    void shouldSaveDocumentSuccessfully() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("document.pdf");
        when(file.getSize()).thenReturn(1024L);
        documentDto.setFile(file);

        when(redisService.getSession("test@gmail.com")).thenReturn(jwtToken);
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(testUser));

        documentService.saveDocument(documentDto, jwtToken);

        verify(file).transferTo(any(Path.class));
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void shouldThrowExceptionForInvalidSession() {
        when(redisService.getSession("test@gmail.com")).thenReturn("different-token");

        UserException exception = assertThrows(UserException.class,
                () -> documentService.saveDocument(documentDto, jwtToken));
        assertEquals(Constants.INVALID_SESSION, exception.getMessage());
    }

    @Test
    void shouldViewDocumentsSuccessfully() {
        Document document = Document.builder()
                .id(1L)
                .type("Passport")
                .filePath("/uploads/document.pdf")
                .uploadedAt(LocalDateTime.now())
                .user(testUser).build();

        when(redisService.getSession("test@gmail.com")).thenReturn(jwtToken);
        when(otpService.validateOtp("test@gmail.com", "123456")).thenReturn(true);
        when(documentRepository.findByUserId(1L)).thenReturn(List.of(document));

        UserDto userDto = new UserDto();
        userDto.setEmail("test@gmail.com");
        userDto.setUserId(1L);
        userDto.setOtp("123456");

        List<DocumentDto> documents = documentService.viewDocuments(userDto, jwtToken);
        assertEquals(1, documents.size());
        assertEquals("Passport", document.getType());
    }

    @Test
    void shouldThrowExceptionForInvalidOtp() {
        when(redisService.getSession("test@gmail.com")).thenReturn(jwtToken);
        when(otpService.validateOtp("test@gmail.com", "123456")).thenReturn(false);

        UserDto userDto = new UserDto();
        userDto.setEmail("test@gmail.com");
        userDto.setUserId(1L);
        userDto.setOtp("123456");

        UserException exception = assertThrows(UserException.class,
                () -> documentService.viewDocuments(userDto, jwtToken));
        assertEquals(Constants.INVALID_OTP, exception.getMessage());
    }

    @Test
    void shouldSendOtpSuccessfully() {
        when(redisService.getSession("test@gmail.com")).thenReturn(jwtToken);
        when(otpRateLimiterService.isOtpRequestAllowed("test@gmail.com")).thenReturn(true);
        when(otpService.generateOtp("test@gmail.com")).thenReturn("123456");

        String result = documentService.sendViewDocumentOtp("test@gmail.com", jwtToken);
        assertEquals(Constants.SEND_OTP, result);
    }
}
