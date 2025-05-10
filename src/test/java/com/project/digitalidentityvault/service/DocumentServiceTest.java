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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private DocumentService documentService;
    private User testUser;
    private DocumentDto request;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .verified(true)
                .build();

        request = new DocumentDto();
        request.setEmail("test@gmail.com");
        request.setType("Passport");
    }

    @Test
    void shouldSaveDocumentSuccessfully() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("passport.pdf");
        when(mockFile.getSize()).thenReturn(1024L);
        request.setFile(mockFile);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        documentService.saveDocument(request, "valid-token");

        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("invalid@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> documentService.saveDocument(request, "valid-token"));
    }

    @Test
    void shouldThrowExceptionWhenFileNotFound() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true);
        request.setFile(mockFile);
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(testUser));

        FileNotFoundException exception = assertThrows(FileNotFoundException.class,
                () -> documentService.saveDocument(request, "valid-token"));
        assertEquals(Constants.FILE_EMPTY, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidFileFormat() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("document.exe");
        request.setFile(mockFile);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));

        assertThrows(FileUploadException.class,
                () -> documentService.saveDocument(request, "valid-token"));
    }

    @Test
    void shouldFetchDocumentsSuccessfully() {
        Document document = Document.builder()
                .id(1L)
                .type("Passport")
                .filePath("path/to/passport.pdf")
                .uploadedAt(LocalDateTime.now())
                .user(testUser)
                .build();

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(testUser));
        when(documentRepository.findByUserId(testUser.getId())).thenReturn(List.of(document));

        List<Document> documents = documentService.viewDocuments(request, "valid-token");

        assertEquals(1, documents.size());
        assertEquals("Passport", documents.get(0).getType());
    }

    @Test
    void shouldThrowExceptionForInvalidSession() {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(testUser));

        assertThrows(UserException.class,
                () -> documentService.viewDocuments(request, "invalid-token"));
    }

//    @Test
//    void shouldVerifyOtpSuccessfully() {
//        String otp = "12345";
//        when(userService.verifyOtp(any(UserDto.class))).thenReturn(Constants.OTP_VERIFIED);
//
//        String result = documentService.verifyOtp("test@gmail.com", otp);
//
//        assertEquals(Constants.OTP_VERIFIED, result);
//    }

//    @Test
//    void shouldThrowExceptionForInvalidOtp() {
//        String otp = "invalid-otp";
//        when(userService.verifyOtp(any(UserDto.class))).thenThrow(new UserException(Constants.INVALID_OTP));
//
//        assertThrows(UserException.class,
//                () -> documentService.verifyOtp("test@gmail.com", otp));
//    }

    @Test
    void shouldSaveDocumentWhenValidFile() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getOriginalFilename()).thenReturn("profile.png");
        request.setFile(mockFile);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        documentService.saveDocument(request, "valid-token");

        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void shouldThrowExceptionIfFileExtensionInvalid() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("document.exe");
        request.setFile(mockFile);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));

        assertThrows(FileUploadException.class,
                () -> documentService.saveDocument(request, "valid-token"));
    }
}