package com.project.digitalidentityvault.util;

import java.util.List;

public class Constants {

    public static final String EMAIL_INVALID = "Enter Valid Email";
    public static final String INVALID_PASSWORD = "Enter Valid Password";
    public static final String INVALID_OTP =  "Enter Valid OTP";
    public static final String REGISTERED_SUCCESSFULLY = "Registered Successfully";
    public static final String LOGIN_SUCCESSFULLY = "Login Successfully";
    public static final String DOCUMENT_UPLOAD = "Document Uploaded Successfully";
    public static final String USER_EXISTED = "User Already Registered";
    public static final String USER_NOT_FOUND = "User Not Found";
    public static final String SEND_OTP = "OTP Sent Successfully";
    public static final String USER_LOGOUT = "Logged out successfully";
    public static final Long DOCUMENT_UPLOAD_MAX_SIZE_MB = 10*1024*1024L;
    public static final List<String> FILE_EXTENSION= List.of("png", "jpg", "jpeg", "pdf");
    public static final String FILE_EMPTY = "File is empty. Please re-upload";
    public static final String FILE_UPLOAD_SIZE_EXCEEDS = "Document size should be less than 10 mb";
    public static final String INVALID_FILE_FORMAT = "Invalid file format. Only PNG, JPG, JPEG, PDF are allowed.";
    public static final String OTP_REQUEST_EXCEED = "You have reached the maximum OTP attempts. Please try again after 24 hours.";
    public static final String INVALID_SESSION = "Invalid Session Token";
    public static final String OTP_VERIFIED = "OTP verified successfully";
}
